package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.packoptimization.CcLevelConstraints;
import com.walmart.aex.sp.dto.packoptimization.Constraints;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import com.walmart.aex.sp.repository.StylePackOptimizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PackOptimizationService {

	@Autowired
	private com.walmart.aex.sp.repository.PackOptimizationRepository packOptRepo;

	@Autowired
	private com.walmart.aex.sp.repository.FinelinePackOptRepository packOptfineplanRepo;

	@Autowired
	private CcPackOptimizationRepository ccPackOptimizationRepository;

	@Autowired
	private StylePackOptimizationRepository stylePackOptimizationRepository;

	Function<Object, String> ifNullThenEmpty = o -> Objects.nonNull(o) ? o.toString() : "";
	private FineLineMapperDto prepareFineLineMapperDto(Object[] object){
		FineLineMapperDto fineLineMapperDto = new FineLineMapperDto();
		fineLineMapperDto.setPlanId(Long.valueOf(ifNullThenEmpty.apply(object[0])));
		fineLineMapperDto.setChannelId(Integer.valueOf(ifNullThenEmpty.apply(object[1])));
		fineLineMapperDto.setLvl0Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[2])));
		fineLineMapperDto.setLvl1Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[3])));
		fineLineMapperDto.setLvl2Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[4])));
		fineLineMapperDto.setLvl3Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[5])));
		fineLineMapperDto.setLvl4Nbr(String.valueOf(ifNullThenEmpty.apply(object[6])));
		fineLineMapperDto.setFineLineNbr(Integer.valueOf(object[7].toString()));
		fineLineMapperDto.setAltfineLineDesc(ifNullThenEmpty.apply(object[8]));

		fineLineMapperDto.setLvl0Desc(ifNullThenEmpty.apply(object[9]));
		fineLineMapperDto.setLvl1Desc(ifNullThenEmpty.apply(object[10]));
		fineLineMapperDto.setLvl2Desc(ifNullThenEmpty.apply(object[11]));
		fineLineMapperDto.setLvl3Desc(ifNullThenEmpty.apply(object[12]));
		fineLineMapperDto.setLvl4Desc(ifNullThenEmpty.apply(object[13]));
		fineLineMapperDto.setFirstName(ifNullThenEmpty.apply(object[15]));
		fineLineMapperDto.setLastName(ifNullThenEmpty.apply(object[16]));
		fineLineMapperDto.setStartTs((Date)object[17]);
		fineLineMapperDto.setReturnMessage(ifNullThenEmpty.apply(object[19]));
		return fineLineMapperDto;
	}
	private void prepareCcPackOptimizationID() {
		CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID();
		StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
		stylePackOptimizationID.setStyleNbr("");
		fineLinePackOptimizationID fineLinePackOptimizationID = new fineLinePackOptimizationID();
		fineLinePackOptimizationID.setFinelineNbr(0);
		SubCatgPackOptimizationID subCatgPackOptimizationID = new SubCatgPackOptimizationID();
		MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID();
		merchantPackOptimizationID.setRepTLvl1(0);
		merchantPackOptimizationID.setRepTLvl2(0);
		merchantPackOptimizationID.setRepTLvl3(0);
		subCatgPackOptimizationID.setMerchantPackOptimizationID(merchantPackOptimizationID);
		fineLinePackOptimizationID.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
		stylePackOptimizationID.setFinelinePackOptimizationID(fineLinePackOptimizationID);
		ccPackOptimizationID.setStylePackOptimizationID(stylePackOptimizationID);
		};
	@Autowired
	private AnalyticsMlSendRepository analyticsMlSendRepository;


	public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelid)
	{
		List<FineLineMapperDto> finePlanPackOptimizationList = packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelid);
		Map<String, List<FineLineMapperDto>> map = finePlanPackOptimizationList.stream().collect(Collectors.groupingBy(f -> f.getPlanId().toString().concat(f.getChannelId().toString())));
		finePlanPackOptimizationList = map.keySet().stream().map(k -> map.get(k).stream().sorted(Comparator.comparing(FineLineMapperDto::getStartTs).reversed()).collect(Collectors.toList()).get(0)).collect(Collectors.toList());
		Set<StylePackOptimization> stylePkOptList = Collections.emptySet();
		List<CcPackOptimization> ccPkOptList = Collections.emptyList();
		return packOptDetails( finePlanPackOptimizationList, stylePkOptList,ccPkOptList, planId, channelid);

	}



	public PackOptimizationResponse packOptDetails(
												   List<FineLineMapperDto> fineLineMapperDtos,
												   Set<StylePackOptimization> stylePkOptList,
												   List<CcPackOptimization> ccPkOptList,
												   Long planId,
												   Integer channelId)
	{

		PackOptimizationResponse packOptResp = new PackOptimizationResponse();
		if(!fineLineMapperDtos.isEmpty()) {
			packOptResp.setPlanId(fineLineMapperDtos.get(0).getPlanId());
			packOptResp.setChannel(fineLineMapperDtos.get(0).getChannelId());
		} else{
			packOptResp.setPlanId(planId);
			packOptResp.setChannel(channelId);
		}
		Function<Integer, List<CustomerChoice>> prepareCCPack = i ->
				ccPkOptList.stream()
						.filter(ccPackOptimization -> ccPackOptimization.getCcPackOptimizationId()
								.getStylePackOptimizationID().getStyleNbr().equals(i))
						.map(ccPackOptimization -> {
					CustomerChoice customerChoice = new CustomerChoice();
					customerChoice.setCcId(ccPackOptimization.getCcPackOptimizationId().getCustomerChoice());
					return customerChoice;
				}).collect(Collectors.toList());

		Function<Integer, List<Style>> prepareStyles = i ->
			stylePkOptList.stream()
					.filter(stylePackOptimization -> stylePackOptimization.getStylepackoptimizationId().getFinelinePackOptimizationID().getFinelineNbr() == i)
					.map(stylePackOptimization -> {
						Style style = new Style();
						style.setStyleNbr(stylePackOptimization.getStylepackoptimizationId().getStyleNbr());
						style.setCustomerChoices(prepareCCPack.apply(0));
						return style;
					}).collect(Collectors.toList());



		BiFunction<List<FineLineMapperDto>, FineLineMapperDto, List<Fineline>> prepareFineLines = (fineLineMapperList, fineLineMapperDto ) -> {
			Function<FineLineMapperDto, List<RunOptimization>> prepareRunOptimizations = flDto -> fineLineMapperList.stream()
					.filter(f -> f.getLvl3Nbr() == flDto.getLvl3Nbr())
					.sorted(Comparator.comparing(FineLineMapperDto::getStartTs).reversed())
					.map(f ->{
				RunOptimization opt = new RunOptimization();
				opt.setName(f.getFirstName()+ "," + f.getLastName());
				opt.setReturnMessage(f.getRunStatusDesc());
				opt.setRunStatusCode(f.getRunStatusCode());
				opt.setStartTs(f.getStartTs());
				return opt;
			}).collect(Collectors.toList());

			return fineLineMapperList.stream().filter(f -> f.getLvl3Nbr() == fineLineMapperDto.getLvl3Nbr()).map(f -> {
				Fineline fineline = new Fineline();
				fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
				fineline.setPackOptimizationStatus(f.getFineLineDesc());
				fineline.setOptimizationDetails(Arrays.asList(prepareRunOptimizations.apply(f).get(0)));
				fineline.setStyles(prepareStyles.apply(fineLineMapperDto.getFineLineNbr()));
				return fineline;
			}).collect(Collectors.toList());
		};
		BiFunction<List<FineLineMapperDto>, FineLineMapperDto, List<Lvl4>> prepareLvl4s = (fineLineMapperList, fineLineMapperDto ) ->
				fineLineMapperList.stream().filter(f -> f.getLvl3Nbr() == fineLineMapperDto.getLvl3Nbr()).map(f->{
					Lvl4 lvl4 = new Lvl4();
					lvl4.setLvl4Nbr(Integer.parseInt(fineLineMapperDto.getLvl4Nbr()));
					lvl4.setFinelines(prepareFineLines.apply(fineLineMapperList, f));
					return lvl4;
				}).collect(Collectors.toList());
   		List<Lvl3> lvl3List1 = fineLineMapperDtos.stream().map(fineLineMapperDto -> {
			Lvl3 lvl3=new Lvl3();
			lvl3.setLvl3Nbr(fineLineMapperDto.getLvl3Nbr());
			List<Lvl4> lvl4List= prepareLvl4s.apply(fineLineMapperDtos, fineLineMapperDto);
			lvl3.setLvl4List(lvl4List);
			return lvl3;
		}).collect(Collectors.toList());
		   packOptResp.setLvl3List(lvl3List1);
		return packOptResp;
	}

	public Constraints getConstraintsDetails(SubCatgPackOptimization subCtgPkopt) {

		Constraints cList= new Constraints();
		SupplierConstraints spList=new SupplierConstraints();
		spList.setSupplierName(subCtgPkopt.getVendorName()); 
		spList.setMaxPacks(subCtgPkopt.getMaxNbrOfPacks());
		spList.setMaxUnitsPerPack(subCtgPkopt.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(subCtgPkopt.getFactoryId());
		ccLevel.setCountryOfOrigin(subCtgPkopt.getOriginCountryName());
		ccLevel.setPortOfOrigin(subCtgPkopt.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(subCtgPkopt.getSinglePackInd());
		ccLevel.setColorCombination(subCtgPkopt.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(spList);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getMerchantPkOptConstraintDetails(MerchantPackOptimization merchPackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints spList=new SupplierConstraints();
		spList.setSupplierName(merchPackOptObj.getVendorName()); 
		spList.setMaxPacks(merchPackOptObj.getMaxNbrOfPacks());
		spList.setMaxUnitsPerPack(merchPackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(merchPackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(merchPackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(merchPackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(merchPackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(merchPackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(spList);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getFinelinePkOptConstraintDetails(fineLinePackOptimization finelinePackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints spList=new SupplierConstraints();
		spList.setSupplierName(finelinePackOptObj.getVendorName()); 
		spList.setMaxPacks(finelinePackOptObj.getMaxNbrOfPacks());
		spList.setMaxUnitsPerPack(finelinePackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(finelinePackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(finelinePackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(finelinePackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(finelinePackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(finelinePackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(spList);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getStylePkOptConstraintDetails(StylePackOptimization stylePackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints spList=new SupplierConstraints();
		spList.setSupplierName(stylePackOptObj.getVendorName()); 
		spList.setMaxPacks(stylePackOptObj.getMaxNbrOfPacks());
		spList.setMaxUnitsPerPack(stylePackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(stylePackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(stylePackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(stylePackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(stylePackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(stylePackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(spList);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getCcPkOptConstraintDetails(CcPackOptimization ccPackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints spList=new SupplierConstraints();
		spList.setSupplierName(ccPackOptObj.getVendorName()); 
		spList.setMaxPacks(ccPackOptObj.getMaxNbrOfPacks());
		spList.setMaxUnitsPerPack(ccPackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(ccPackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(ccPackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(ccPackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(ccPackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(ccPackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(spList);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public List<Lvl4> subCategoryResponseList(Set<SubCatgPackOptimization> subCatgList, Set<fineLinePackOptimization> finelineList, 
			Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList)
	{
		List<Lvl4> lvl4list=new ArrayList<>();

		for(SubCatgPackOptimization subctgpkopt : subCatgList)
		{
			Lvl4 lvl4=new Lvl4();
			lvl4.setConstraints(getConstraintsDetails(subctgpkopt));

			List<Fineline> fineLinelist = new ArrayList<>();
			fineLinelist = finelineResponseList(finelineList, stylePkOptList, ccPkOptList);
			lvl4.setFinelines(fineLinelist);
			lvl4.setFinelines(fineLinelist);

			lvl4list.add(lvl4);
		}
		return lvl4list;
	}

	public List<Fineline> finelineResponseList(Set<fineLinePackOptimization> finelinePkOptList, 
			Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList)
	{
		List<Fineline> finelineList = new ArrayList<>();

		for(fineLinePackOptimization fineLinePkOpt : finelinePkOptList)
		{
			Fineline fineListObj=new Fineline();
			fineListObj.setFinelineNbr(fineLinePkOpt.getFinelinePackOptId().getFinelineNbr());

			List<Style> styleList = new ArrayList<>();
			styleList = styleResponseList(stylePkOptList, ccPkOptList);
			fineListObj.setConstraints(getFinelinePkOptConstraintDetails(fineLinePkOpt));
			fineListObj.setStyles(styleList);

			finelineList.add(fineListObj);
		}

		return finelineList;
	}

	public List<Style> styleResponseList(Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList)
	{
		List<Style> styleList = new ArrayList<>();

		for(StylePackOptimization stylePkOptObj : stylePkOptList)
		{
			Style style = new Style();
			List<CustomerChoice> customerChoiceList = new ArrayList();
			customerChoiceList = customerChoiceResponseList(ccPkOptList);

			style.setStyleNbr(stylePkOptObj.getStylepackoptimizationId().getStyleNbr());
			style.setConstraints(getStylePkOptConstraintDetails(stylePkOptObj));
			style.setCustomerChoices(customerChoiceList);
			styleList.add(style);

		}

		return styleList;
	}

	public List<CustomerChoice> customerChoiceResponseList(Set<CcPackOptimization> ccPkOptList)
	{
		List<CustomerChoice> customerChoiceList = new ArrayList();

		for(CcPackOptimization ccpkOptObj : ccPkOptList)
		{
			CustomerChoice customerChoice = new CustomerChoice();
			customerChoice.setCcId(ccpkOptObj.getCcPackOptimizationId().getCustomerChoice());
			customerChoice.setColorName(null);
			customerChoice.setConstraints(getCcPkOptConstraintDetails(ccpkOptObj));
			customerChoiceList.add(customerChoice);

		}

		return customerChoiceList;
	}

	public void UpdatePkOptServiceStatus(Long planId, Integer finelineNbr, Integer status) {
		analyticsMlSendRepository.updateStatus(planId, finelineNbr, status);
	}
}
