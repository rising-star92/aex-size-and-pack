package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.packoptimization.CcLevelConstraints;
import com.walmart.aex.sp.dto.packoptimization.Constraints;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.SupplierConstraints;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.StylePackOptimization;
import com.walmart.aex.sp.entity.SubCatgPackOptimization;
import com.walmart.aex.sp.entity.fineLinePackOptimization;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.FineLinePackOptimizationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PackOptimizationService {

	@Autowired
	private com.walmart.aex.sp.repository.PackOptimizationRepository packOptRepo;
	
	private final FineLinePackOptimizationRepository finelinePackOptimizationRepository;

	private final PackOptimizationMapper packOptimizationMapper;

	public PackOptimizationService(FineLinePackOptimizationRepository finelinePackOptimizationRepository,PackOptimizationMapper packOptimizationMapper) {
		this.finelinePackOptimizationRepository = finelinePackOptimizationRepository;
		this.packOptimizationMapper = packOptimizationMapper;
	}


	public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelid)
	{

		List<MerchantPackOptimization> merchantPackOptimizationList = packOptRepo.findByMerchantPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelid);

		Set<SubCatgPackOptimization> subCatgList = merchantPackOptimizationList.stream().flatMap(merchantpackoptimization -> merchantpackoptimization.getSubCatgPackOptimization().stream()).collect(Collectors.toSet());

		Set<fineLinePackOptimization> finelineList = subCatgList.stream().flatMap(subctgpackoptimization -> subctgpackoptimization.getFinelinepackOptimization().stream()).collect(Collectors.toSet());

		Set<StylePackOptimization> stylePkOptList = finelineList.stream().flatMap(fineLinePackOptimization -> fineLinePackOptimization.getStylePackOptimization().stream()).collect(Collectors.toSet());

		Set<CcPackOptimization> ccPkOptList = stylePkOptList.stream().flatMap(stylePackOptimization -> stylePackOptimization.getCcPackOptimization().stream()).collect(Collectors.toSet());



		return packOptListDetails(merchantPackOptimizationList, subCatgList, finelineList, stylePkOptList, ccPkOptList);

	}

	public PackOptimizationResponse packOptListDetails(List<MerchantPackOptimization> merchantPackOptimizationList, Set<SubCatgPackOptimization> subCatgList, 
			Set<fineLinePackOptimization> finelineList, Set<StylePackOptimization> stylePkOptList, 
			Set<CcPackOptimization> ccPkOptList)
	{
		int index = 0;
		MerchantPackOptimization merchantpackOptObj = merchantPackOptimizationList.get(index);
		PackOptimizationResponse packOptResp = packOptDetails(merchantpackOptObj, subCatgList, finelineList, stylePkOptList, ccPkOptList);
		return packOptResp;
	}

	public PackOptimizationResponse packOptDetails(MerchantPackOptimization merchPackOptObj, Set<SubCatgPackOptimization> subCatgList, 
			Set<fineLinePackOptimization> finelineList, Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList)
	{
		PackOptimizationResponse packOptResp = new PackOptimizationResponse();
		packOptResp.setPlanId(merchPackOptObj.getMerchantPackOptimizationID().getPlanId());
		packOptResp.setChannel(merchPackOptObj.getChannelText().getChannelId());

		List<Lvl3> lvl3List=new ArrayList<>();
		Lvl3 lvl3=new Lvl3();
		lvl3.setLvl3Nbr(merchPackOptObj.getMerchantPackOptimizationID().getRepTLvl3());
		lvl3.setConstraints(getMerchantPkOptConstraintDetails(merchPackOptObj));
		lvl3List.add(lvl3);

		List<Lvl4> lvl4List=new ArrayList<>();
		lvl4List = subCategoryResponseList(subCatgList, finelineList, stylePkOptList, ccPkOptList);

		lvl3.setLvl4List(lvl4List);
		packOptResp.setLvl3List(lvl3List);

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
	
	
	public FineLinePackOptimizationResponse getPackOptFinelineDetails(Long planId, Integer finelineNbr)
	{
		FineLinePackOptimizationResponse finelinePackOptimizationResponse = new FineLinePackOptimizationResponse();

		try {
			List<FineLinePackOptimizationResponseDTO> finelinePackOptimizationResponseDTOS = finelinePackOptimizationRepository.getPackOptByFineline(planId, finelineNbr);
			Optional.of(finelinePackOptimizationResponseDTOS)
			.stream()
			.flatMap(Collection::stream)
			.forEach(FinelinePackOptimizationResponseDTO -> packOptimizationMapper.
					mapPackOptimizationFineline(FinelinePackOptimizationResponseDTO, finelinePackOptimizationResponse));


		} catch (Exception e) {
			log.error("Exception While fetching Fineline pack Optimization :", e);
			throw new CustomException("Failed to fetch Fineline Pack Optimization , due to" + e);
		}
		log.info("Fetch Pack Optimization Fineline response: {}", finelinePackOptimizationResponse);
		return finelinePackOptimizationResponse;
	}





}
