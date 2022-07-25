package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.packOptimization.PackOptimizationResponse;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import com.walmart.aex.sp.entity.ChannelText;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
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
	private PackOptimizationService packOptimizationService;
	
	@Mock
	private PackOptimizationRepository packOptimizationRepo;
	
	@Mock
	PackOptimizationResponse packOptResponse;
	
	@Mock
	List<MerchantPackOptimization> merchantPackOptimizationlist;
	
	@Test
	public void testGetPackOptDetails()
	{
		Long planId = 362L;
		Integer channelid = 1;
		ChannelText channeltext = new ChannelText();
		channeltext.setChannelId(1);
		channeltext.setChannelDesc("Store");
		
		MerchantPackOptimization merchpackOptObj = new MerchantPackOptimization();
		
		
		merchantPackOptimizationlist = new ArrayList();
		
		MerchantPackOptimizationID merchpackOptID = new MerchantPackOptimizationID();
		
		merchpackOptID.setPlanId(362L);
		merchpackOptID.setRepTLvl0(0);
		merchpackOptID.setRepTLvl1(0);
		merchpackOptID.setRepTLvl2(0);
		merchpackOptID.setRepTLvl3(25);
		
		merchpackOptObj.setMerchantPackOptimizationID(merchpackOptID);
		merchpackOptObj.setChannelText(channeltext);
		
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
		
		
		subcatgpkoptlist.add(subctgOptObj);
		
		merchpackOptObj.setSubCatgPackOptimization(subcatgpkoptlist);
		
		merchantPackOptimizationlist.add(merchpackOptObj);
		
		Mockito.when(packOptimizationRepo.findByMerchantPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelid)).thenReturn(merchantPackOptimizationlist);
		packOptResponse = packOptimizationService.getPackOptDetails(362L, 1);
		
		
		assertNotNull(packOptResponse);
		assertEquals(packOptResponse.getPlanId(), 362L);
	    
		
	}

}
