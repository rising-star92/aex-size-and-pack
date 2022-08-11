package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.repository.FinelinePackOptRepository;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import com.walmart.aex.sp.entity.ChannelText;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import com.walmart.aex.sp.entity.StylePackOptimization;
import com.walmart.aex.sp.entity.StylePackOptimizationID;
import com.walmart.aex.sp.entity.SubCatgPackOptimization;
import com.walmart.aex.sp.entity.SubCatgPackOptimizationID;
import com.walmart.aex.sp.entity.fineLinePackOptimization;
import com.walmart.aex.sp.entity.fineLinePackOptimizationID;
import com.walmart.aex.sp.repository.PackOptimizationRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PackOptimizationServiceTest {

	@InjectMocks
	@Spy
	private PackOptimizationService packOptimizationService;

	@Mock
	private AnalyticsMlSendRepository analyticsMlSendRepository;
	

	@Mock
	PackOptimizationResponse packOptResponse;

	@Mock
	FinelinePackOptRepository packOptfineplanRepo;

	@Test
	public void testGetPackOptDetails()
	{
		Long planId = 362L;
		Integer channelid = 1;
		ChannelText channeltext = new ChannelText();
		channeltext.setChannelId(1);
		channeltext.setChannelDesc("Store");

		FineLineMapperDto merchpackOptObj = new FineLineMapperDto();


		List<FineLineMapperDto>  merchantPackOptimizationlist = new ArrayList();

		MerchantPackOptimizationID merchpackOptID = new MerchantPackOptimizationID();

		merchpackOptObj.setPlanId(362L);
		merchpackOptObj.setLvl0Nbr(0);
		merchpackOptObj.setLvl1Nbr(0);
		merchpackOptObj.setLvl2Nbr(0);
		merchpackOptObj.setLvl3Nbr(25);
		merchpackOptObj.setLvl4Nbr("252");
		merchpackOptObj.setChannelId(1);

		SubCatgPackOptimization subctgOptObj = new SubCatgPackOptimization();
		SubCatgPackOptimizationID subctgOptID = new SubCatgPackOptimizationID();
		subctgOptID.setMerchantPackOptimizationID(merchpackOptID);
		subctgOptObj.setSubCatgPackOptimizationID(subctgOptID);

		Set<SubCatgPackOptimization> subcatgpkoptlist = new LinkedHashSet<>();


		fineLinePackOptimizationID finelinepkOptID = new fineLinePackOptimizationID();
		finelinepkOptID.setSubCatgPackOptimizationID(subctgOptID);
		fineLinePackOptimization finelinepkOptObj = new fineLinePackOptimization();
		finelinepkOptObj.setFinelinePackOptId(finelinepkOptID);

		Set<fineLinePackOptimization> finelinepkoptlist = new LinkedHashSet<>();
		StylePackOptimizationID stylepkOptID = new StylePackOptimizationID();
		stylepkOptID.setFinelinePackOptimizationID(finelinepkOptID);
		StylePackOptimization stylepkOptObj= new StylePackOptimization();
		stylepkOptObj.setStylepackoptimizationId(stylepkOptID);

		Set<StylePackOptimization> stylepkoptlist = new LinkedHashSet<>();
		finelinepkOptObj.setStylePackOptimization(stylepkoptlist);
		stylepkoptlist.add(stylepkOptObj);
		finelinepkOptObj.setStylePackOptimization(stylepkoptlist);
		finelinepkoptlist.add(finelinepkOptObj);

		CcPackOptimizationID ccpkoptId= new CcPackOptimizationID();
		ccpkoptId.setStylePackOptimizationID(stylepkOptID);
		CcPackOptimization ccpkOptObj= new CcPackOptimization();
		ccpkOptObj.setCcPackOptimizationId(ccpkoptId);
		Set<CcPackOptimization> ccpkoptlist = new LinkedHashSet<>();
		ccpkoptlist.add(ccpkOptObj);
		stylepkOptObj.setCcPackOptimization(ccpkoptlist);
		stylepkoptlist.add(stylepkOptObj);


		subctgOptObj.setFinelinepackOptimization(finelinepkoptlist);


		merchantPackOptimizationlist.add(merchpackOptObj);

		Mockito.when(packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelid)).thenReturn(merchantPackOptimizationlist);
		packOptResponse = packOptimizationService.getPackOptDetails(362L, 1);


		assertNotNull(packOptResponse);
		assertEquals(packOptResponse.getPlanId(), 362L);


	}

	@Test
	public void testUpdateRunStatusCode()
	{
		Long planId = 234L;
		Integer finelineNbr = 46;
		Integer status = 10;
		packOptimizationService.UpdatePkOptServiceStatus(planId, finelineNbr, status);
		Mockito.verify(packOptimizationService,Mockito.times(1)).UpdatePkOptServiceStatus(planId, finelineNbr, status);
		assertEquals(finelineNbr,46);
	}
}
