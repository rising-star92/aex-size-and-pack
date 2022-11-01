package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.Constraints;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import com.walmart.aex.sp.entity.ChannelText;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import com.walmart.aex.sp.entity.StylePackOptimization;
import com.walmart.aex.sp.entity.StylePackOptimizationID;
import com.walmart.aex.sp.entity.SubCatgPackOptimization;
import com.walmart.aex.sp.entity.SubCatgPackOptimizationID;
import com.walmart.aex.sp.entity.FineLinePackOptimization;
import com.walmart.aex.sp.entity.FineLinePackOptimizationID;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import com.walmart.aex.sp.repository.FineLinePackOptimizationRepository;
import com.walmart.aex.sp.repository.FinelinePackOptRepository;

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
	
	@Mock
	Set<SubCatgPackOptimization> subCatgList;
	
	@Mock
	Set<FineLinePackOptimization> finelineList;
	
	@Mock
	Set<StylePackOptimization> stylePkOptList;
	
	@Mock
	Set<CcPackOptimization> ccPkOptList;
	
	@Mock
	FineLinePackOptimizationRepository finelinePackOptimizationRepository;
	@Mock
	PackOptimizationMapper packOptimizationMapper;
	
	@Mock
	FineLinePackOptimizationResponse finelinePackOptimizationResponse;
	
	@Mock
	List<FineLinePackOptimizationResponseDTO> finelinePackOptimizationResponseList;

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
		merchpackOptObj.setLvl4Nbr(252);
		merchpackOptObj.setFineLineNbr(2542);
		merchpackOptObj.setChannelId(1);

		SubCatgPackOptimization subctgOptObj = new SubCatgPackOptimization();
		SubCatgPackOptimizationID subctgOptID = new SubCatgPackOptimizationID();
		subctgOptID.setMerchantPackOptimizationID(merchpackOptID);
		subctgOptObj.setSubCatgPackOptimizationID(subctgOptID);

		Set<SubCatgPackOptimization> subcatgpkoptlist = new LinkedHashSet<>();


		FineLinePackOptimizationID finelinepkOptID = new FineLinePackOptimizationID();
		finelinepkOptID.setSubCatgPackOptimizationID(subctgOptID);
		FineLinePackOptimization finelinepkOptObj = new FineLinePackOptimization();
		finelinepkOptObj.setFinelinePackOptId(finelinepkOptID);

		Set<FineLinePackOptimization> finelinepkoptlist = new LinkedHashSet<>();
		StylePackOptimizationID stylepkOptID = new StylePackOptimizationID();
		stylepkOptID.setFinelinePackOptimizationID(finelinepkOptID);
		StylePackOptimization stylepkOptObj= new StylePackOptimization();
		stylepkOptObj.setStylePackoptimizationId(stylepkOptID);

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
	
	@Test
	public void testSubCategoryResponseList() {
		subCatgList = new HashSet<SubCatgPackOptimization>();
		SubCatgPackOptimization subCtgPkopt = new SubCatgPackOptimization();
		subCtgPkopt.setVendorName("walmart");
		subCtgPkopt.setMaxNbrOfPacks(3);
		subCtgPkopt.setMaxUnitsPerPack(1);
		subCtgPkopt.setFactoryId("123");
		subCtgPkopt.setPortOfOriginName("cc");
		subCatgList.add(subCtgPkopt);

		finelineList = new HashSet<FineLinePackOptimization>();
		FineLinePackOptimization finelinePackOptObj = new FineLinePackOptimization();
		FineLinePackOptimizationID fineLinePackOptimizationID = new FineLinePackOptimizationID();
		finelinePackOptObj.setFinelinePackOptId(fineLinePackOptimizationID);
		fineLinePackOptimizationID.setFinelineNbr(5147);
		finelinePackOptObj.setVendorName("walmart");
		finelinePackOptObj.setMaxNbrOfPacks(1);
		finelinePackOptObj.setMaxUnitsPerPack(1);
		finelinePackOptObj.setFactoryId("123");
		finelinePackOptObj.setPortOfOriginName("cc");
		finelineList.add(finelinePackOptObj);

		stylePkOptList = new HashSet<StylePackOptimization>();
		StylePackOptimization stylePackOptObj = new StylePackOptimization();
		StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
		stylePackOptObj.setStylePackoptimizationId(stylePackOptimizationID);
		stylePackOptimizationID.setStyleNbr("34_2968_3_18_2");
		stylePackOptObj.setVendorName("walmart");
		stylePackOptObj.setMaxNbrOfPacks(3);
		stylePackOptObj.setMaxUnitsPerPack(1);
		stylePackOptObj.setFactoryId("123");
		stylePackOptObj.setPortOfOriginName("cc");
		stylePkOptList.add(stylePackOptObj);

		ccPkOptList = new HashSet<CcPackOptimization>();
		CcPackOptimization ccPackOptObj = new CcPackOptimization();
		CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID();
		ccPackOptObj.setCcPackOptimizationId(ccPackOptimizationID);
		ccPackOptimizationID.setCustomerChoice("34_2968_3_18_2_BLACK SOOT");
		ccPackOptObj.setVendorName("walmart");
		ccPackOptObj.setMaxNbrOfPacks(1);
		ccPackOptObj.setMaxUnitsPerPack(1);
		ccPackOptObj.setFactoryId("123");
		ccPackOptObj.setPortOfOriginName("cc");
		ccPkOptList.add(ccPackOptObj);
		List<Lvl4> lvl4s = packOptimizationService.subCategoryResponseList(subCatgList, finelineList, stylePkOptList,
				ccPkOptList);
		assertEquals(5147, lvl4s.get(0).getFinelines().get(0).getFinelineNbr());
		assertEquals("34_2968_3_18_2", lvl4s.get(0).getFinelines().get(0).getStyles().get(0).getStyleNbr());
		assertEquals("34_2968_3_18_2_BLACK SOOT",
				lvl4s.get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getCcId());

	}

	@Test
	public void testGetPackOptFinelineDetails() {
		finelinePackOptimizationResponseList = new ArrayList<FineLinePackOptimizationResponseDTO>();
		FineLinePackOptimizationResponseDTO fineLinePackOptimizationResponse = new FineLinePackOptimizationResponseDTO();
		fineLinePackOptimizationResponse.setPlanId(483l);
		fineLinePackOptimizationResponse.setAhsSizeDesc("XL");
		fineLinePackOptimizationResponse.setCcId("34_5147_3_21_4_SEA TURTLE/DARK NAVY");
		fineLinePackOptimizationResponse.setFinelineNbr(5147);
		fineLinePackOptimizationResponse.setFixtureTypeRollupName("Walls");
		fineLinePackOptimizationResponse.setMerchMethod(1);
		fineLinePackOptimizationResponse.setPlanDesc("Black");
		finelinePackOptimizationResponseList.add(fineLinePackOptimizationResponse);
		lenient().doNothing().when(packOptimizationMapper).mapPackOptimizationFineline(fineLinePackOptimizationResponse,
				finelinePackOptimizationResponse, 483l);
		Mockito.when(finelinePackOptimizationRepository.getPackOptByFineline(483l, 5147))
				.thenReturn(finelinePackOptimizationResponseList);
		finelinePackOptimizationResponse = packOptimizationService.getPackOptFinelineDetails(483l, 5147);
		Mockito.verify(packOptimizationMapper, Mockito.times(1)).mapPackOptimizationFineline(Mockito.any(),
				Mockito.any(), Mockito.any());
		assertNotNull(fineLinePackOptimizationResponse);
	}	
	
	@Test
	public void testGetMerchantPkOptConstraintDetails() {
		MerchantPackOptimization merchPackOptObj = new MerchantPackOptimization();
		merchPackOptObj.setVendorName("walmart");
		merchPackOptObj.setMaxNbrOfPacks(3);
		merchPackOptObj.setMaxUnitsPerPack(1);
		merchPackOptObj.setFactoryId("123");
		merchPackOptObj.setPortOfOriginName("cc");
		Constraints constraints = packOptimizationService.getMerchantPkOptConstraintDetails(merchPackOptObj);
		assertEquals("123", constraints.getCcLevelConstraints().getFactoryIds());
		assertEquals(3, constraints.getSupplierConstraints().getMaxPacks());
		assertEquals(1, constraints.getSupplierConstraints().getMaxUnitsPerPack());
	}	
}
