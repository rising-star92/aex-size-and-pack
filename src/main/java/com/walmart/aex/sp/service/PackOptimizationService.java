package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.CcLevelConstraints;
import com.walmart.aex.sp.dto.Constraints;
import com.walmart.aex.sp.dto.CustomerChoice;
import com.walmart.aex.sp.dto.Fineline;
import com.walmart.aex.sp.dto.Lvl3;
import com.walmart.aex.sp.dto.Lvl4;
import com.walmart.aex.sp.dto.PackOptimizationResponse;
import com.walmart.aex.sp.dto.Style;
import com.walmart.aex.sp.dto.SupplierConstraints;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import com.walmart.aex.sp.entity.StylePackOptimization;
import com.walmart.aex.sp.entity.SubCatgPackOptimization;
import com.walmart.aex.sp.entity.fineLinePackOptimization;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PackOptimizationService {

	@Autowired
	private com.walmart.aex.sp.repository.PackOptimizationRepository packOptRepo;


	public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelid)
	{

		List<MerchantPackOptimization> merchantPackOptimizationlist = packOptRepo.findByMerchantPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelid);

		Set<SubCatgPackOptimization> subcatglist = merchantPackOptimizationlist.stream().flatMap(merchantpackoptimization -> merchantpackoptimization.getSubCatgPackOptimization().stream()).collect(Collectors.toSet());

		Set<fineLinePackOptimization> finelinelist = subcatglist.stream().flatMap(subctgpackoptimization -> subctgpackoptimization.getFinelinepackOptimization().stream()).collect(Collectors.toSet());

		Set<StylePackOptimization> stylepkOptlist = finelinelist.stream().flatMap(fineLinePackOptimization -> fineLinePackOptimization.getStylePackOptimization().stream()).collect(Collectors.toSet());

		Set<CcPackOptimization> ccpkOptlist = stylepkOptlist.stream().flatMap(stylePackOptimization -> stylePackOptimization.getCcPackOptimization().stream()).collect(Collectors.toSet());

		System.out.println(merchantPackOptimizationlist);

		System.out.println(subcatglist);

		System.out.println(finelinelist);

		return packOptListDetails(merchantPackOptimizationlist, subcatglist, finelinelist, stylepkOptlist, ccpkOptlist);

	}

	public PackOptimizationResponse packOptListDetails(List<MerchantPackOptimization> merchantPackOptimizationlist, Set<SubCatgPackOptimization> subcatglist, 
			Set<fineLinePackOptimization> finelinelist, Set<StylePackOptimization> stylepkOptlist, 
			Set<CcPackOptimization> ccpkOptlist)
	{
		int index = 0;
		MerchantPackOptimization merchantpackOptObj = merchantPackOptimizationlist.get(index);
		PackOptimizationResponse packOptResp = packOptDetails(merchantpackOptObj, subcatglist, finelinelist, stylepkOptlist, ccpkOptlist);
		return packOptResp;
	}

	public PackOptimizationResponse packOptDetails(MerchantPackOptimization merchpackOptObj, Set<SubCatgPackOptimization> subcatglist, 
			Set<fineLinePackOptimization> finelinelist, Set<StylePackOptimization> stylepkOptlist, Set<CcPackOptimization> ccpkOptlist)
	{
		PackOptimizationResponse packOptResp = new PackOptimizationResponse();
		packOptResp.setPlanId(merchpackOptObj.getMerchantPackOptimizationID().getPlanId());
		packOptResp.setChannel(merchpackOptObj.getChannelText().getChannelId());

		List<Lvl3> lvl3list=new ArrayList<>();
		Lvl3 lvl3=new Lvl3();
		lvl3.setLvl3Nbr(merchpackOptObj.getMerchantPackOptimizationID().getRepTLvl3());
		lvl3.setLvl3Name("Pants Bottoms Sportswear Ec Mens");
		lvl3.setConstraints(getMerchantPkOptConstraintDetails(merchpackOptObj));
		lvl3list.add(lvl3);

		List<Lvl4> lvl4list=new ArrayList<>();
		lvl4list = subcategoryResponseList(subcatglist, finelinelist, stylepkOptlist, ccpkOptlist);

		lvl3.setLvl4List(lvl4list);
		packOptResp.setLvl3list(lvl3list);

		return packOptResp;


	}

	public Constraints getConstraintsDetails(SubCatgPackOptimization subctgpkopt) {

		Constraints cList= new Constraints();
		SupplierConstraints splist=new SupplierConstraints();
		splist.setSupplierName(subctgpkopt.getVendorName()); 
		splist.setMaxPacks(subctgpkopt.getMaxNbrOfPacks());
		splist.setMaxUnitsPerPack(subctgpkopt.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(subctgpkopt.getFactoryId());
		ccLevel.setCountryOfOrigin(subctgpkopt.getOriginCountryName());
		ccLevel.setPortOfOrigin(subctgpkopt.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(subctgpkopt.getSinglePackInd());
		ccLevel.setColorCombination(subctgpkopt.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(splist);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getMerchantPkOptConstraintDetails(MerchantPackOptimization merchpackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints splist=new SupplierConstraints();
		splist.setSupplierName(merchpackOptObj.getVendorName()); 
		splist.setMaxPacks(merchpackOptObj.getMaxNbrOfPacks());
		splist.setMaxUnitsPerPack(merchpackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(merchpackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(merchpackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(merchpackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(merchpackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(merchpackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(splist);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getFinelinePkOptConstraintDetails(fineLinePackOptimization finelinepackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints splist=new SupplierConstraints();
		splist.setSupplierName(finelinepackOptObj.getVendorName()); 
		splist.setMaxPacks(finelinepackOptObj.getMaxNbrOfPacks());
		splist.setMaxUnitsPerPack(finelinepackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(finelinepackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(finelinepackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(finelinepackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(finelinepackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(finelinepackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(splist);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getStylePkOptConstraintDetails(StylePackOptimization stylepackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints splist=new SupplierConstraints();
		splist.setSupplierName(stylepackOptObj.getVendorName()); 
		splist.setMaxPacks(stylepackOptObj.getMaxNbrOfPacks());
		splist.setMaxUnitsPerPack(stylepackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(stylepackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(stylepackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(stylepackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(stylepackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(stylepackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(splist);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public Constraints getCcPkOptConstraintDetails(CcPackOptimization ccpackOptObj) {

		Constraints cList= new Constraints();
		SupplierConstraints splist=new SupplierConstraints();
		splist.setSupplierName(ccpackOptObj.getVendorName()); 
		splist.setMaxPacks(ccpackOptObj.getMaxNbrOfPacks());
		splist.setMaxUnitsPerPack(ccpackOptObj.getMaxUnitsPerPack()); 

		List<CcLevelConstraints> ccLevelList= new ArrayList<>();
		CcLevelConstraints ccLevel=new CcLevelConstraints();
		ccLevel.setFactoryIds(ccpackOptObj.getFactoryId());
		ccLevel.setCountryOfOrigin(ccpackOptObj.getOriginCountryName());
		ccLevel.setPortOfOrigin(ccpackOptObj.getPortOfOriginName());
		ccLevel.setSinglePackIndicator(ccpackOptObj.getSinglePackInd());
		ccLevel.setColorCombination(ccpackOptObj.getColorCombination());
		ccLevelList.add(ccLevel);
		cList.setSupplierConstraints(splist);
		cList.setCcLevelConstraints(ccLevelList);
		return cList;

	}

	public List<Lvl4> subcategoryResponseList(Set<SubCatgPackOptimization> subcatglist, Set<fineLinePackOptimization> finelinelist, 
			Set<StylePackOptimization> stylepkOptlist, Set<CcPackOptimization> ccpkOptlist)
	{
		List<Lvl4> lvl4list=new ArrayList<>();

		for(SubCatgPackOptimization subctgpkopt : subcatglist)
		{
			Lvl4 lvl4=new Lvl4();
			lvl4.setConstraints(getConstraintsDetails(subctgpkopt));

			List<Fineline> fineLinelist = new ArrayList<>();
			fineLinelist = finelineResponseList(finelinelist, stylepkOptlist, ccpkOptlist);
			lvl4.setFinelines(fineLinelist);
			lvl4.setFinelines(fineLinelist);

			lvl4list.add(lvl4);
		}
		return lvl4list;
	}

	public List<Fineline> finelineResponseList(Set<fineLinePackOptimization> finelinepkoptlist, 
			Set<StylePackOptimization> stylepkOptlist, Set<CcPackOptimization> ccpkOptlist)
	{
		List<Fineline> finelinelist = new ArrayList<>();

		for(fineLinePackOptimization finelinepkopt : finelinepkoptlist)
		{
			Fineline finelistobj=new Fineline();
			finelistobj.setFinelineNbr(finelinepkopt.getFinelinePackOptId().getFinelineNbr());

			List<Style> stylelist = new ArrayList<>();
			stylelist = styleResponseList(stylepkOptlist, ccpkOptlist);
			finelistobj.setConstraints(getFinelinePkOptConstraintDetails(finelinepkopt));
			finelistobj.setStyles(stylelist);

			finelinelist.add(finelistobj);
		}

		return finelinelist;
	}

	public List<Style> styleResponseList(Set<StylePackOptimization> stylepkOptlist, Set<CcPackOptimization> ccpkOptlist)
	{
		List<Style> stylelist = new ArrayList<>();

		for(StylePackOptimization stylepkOptObj : stylepkOptlist)
		{
			Style style = new Style();
			List<CustomerChoice> customerChoiceList = new ArrayList();
			customerChoiceList = customerChoiceResponseList(ccpkOptlist);

			style.setStyleNbr(stylepkOptObj.getStylepackoptimizationId().getStyleNbr());
			style.setConstraints(getStylePkOptConstraintDetails(stylepkOptObj));
			style.setCustomerChoices(customerChoiceList);
			stylelist.add(style);

		}

		return stylelist;
	}

	public List<CustomerChoice> customerChoiceResponseList(Set<CcPackOptimization> ccpkOptlist)
	{
		List<CustomerChoice> customerChoiceList = new ArrayList();

		for(CcPackOptimization ccpkOptObj : ccpkOptlist)
		{
			CustomerChoice customerChoice = new CustomerChoice();
			customerChoice.setCcId(ccpkOptObj.getCcPackOptimizationId().getCustomerChoice());
			customerChoice.setColorName(null);
			customerChoice.setConstraints(getCcPkOptConstraintDetails(ccpkOptObj));
			customerChoiceList.add(customerChoice);

		}

		return customerChoiceList;
	}

}
