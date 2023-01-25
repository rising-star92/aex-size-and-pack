package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import com.walmart.aex.sp.repository.FineLinePackOptimizationRepository;
import com.walmart.aex.sp.repository.FinelinePackOptRepository;
import com.walmart.aex.sp.repository.StyleCcPackOptConsRepository;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackOptimizationServiceTest {

    @InjectMocks
    @Spy
    private PackOptimizationService packOptimizationService;

	@Mock
	private AnalyticsMlSendRepository analyticsMlSendRepository;
	@Mock
	private PackOptimizationResponse packOptResponse;
	@Mock
	private FinelinePackOptRepository packOptfineplanRepo;
	@Mock
	private FineLinePackOptimizationRepository finelinePackOptimizationRepository;
	@Mock
	private PackOptimizationMapper packOptimizationMapper;
	@Mock
	private FineLinePackOptimizationResponse finelinePackOptimizationResponse;
	@Mock
	private List<FineLinePackOptimizationResponseDTO> finelinePackOptimizationResponseList;
	@Mock
	private PackOptConstraintMapper packOptConstraintMapper;
   @Mock
    private CcPackOptimizationRepository ccPackOptimizationRepository;
   @Mock
   private StyleCcPackOptConsRepository styleCcPackOptConsRepository;

   @Test
   void test_getPackOptDetailsShouldReturnEmptyResponseWhenDBReturnEmptyList() {
	   when(packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(anyLong(), anyInt())).thenReturn(Collections.emptyList());
	   packOptResponse = packOptimizationService.getPackOptDetails(362L, 1);
	   assertNull(packOptResponse.getPlanId());
	   assertNull(packOptResponse.getChannel());
   }

	@Test
	void test_getPackOptFinelineDetailsShouldReturnEmptyResponseWhenDBReturnEmptyList() {
	   when(finelinePackOptimizationRepository.getPackOptByFineline(anyLong(), anyInt()))
			   .thenReturn(Collections.emptyList());
		finelinePackOptimizationResponse = packOptimizationService.getPackOptFinelineDetails(483L, 5147, 1);
		assertNull(finelinePackOptimizationResponse.getPlanId());
	}

	@Test
	void testGetPackOptDetails() {
		Long planId = 362L;
		Integer channelId = 1;

		FineLineMapperDto merchpackOptObj = new FineLineMapperDto();
		List<FineLineMapperDto> merchantPackOptimizationlist = new ArrayList();

		merchpackOptObj.setPlanId(362L);
		merchpackOptObj.setLvl0Nbr(0);
		merchpackOptObj.setLvl1Nbr(0);
		merchpackOptObj.setLvl2Nbr(0);
		merchpackOptObj.setLvl3Nbr(25);
		merchpackOptObj.setLvl4Nbr(252);
		merchpackOptObj.setFineLineNbr(2542);
		merchpackOptObj.setChannelId(1);
		merchpackOptObj.setMerchFactoryId("120, 121");
		merchpackOptObj.setMerchMaxNbrOfPacks(50);
		merchpackOptObj.setMerchMaxUnitsPerPack(10);
		merchpackOptObj.setMerchColorCombination("Merch Color Combination");
		merchpackOptObj.setMerchSinglePackInd(1);
		merchpackOptObj.setMerchSupplierName("Vendor Name");
		merchpackOptObj.setMerchOriginCountryName("USA");
		merchpackOptObj.setMerchPortOfOriginName("Texas");
		merchpackOptObj.setSubCatFactoryId("120");
		merchpackOptObj.setSubCatMaxNbrOfPacks(20);
		merchpackOptObj.setSubCatMaxUnitsPerPack(10);
		merchpackOptObj.setSubCatColorCombination("Sub Category Color Combination");
		merchpackOptObj.setSubCatSinglePackInd(1);
		merchpackOptObj.setSubCatSupplierName("Supplier Vendor Name");
		merchpackOptObj.setSubCatOriginCountryName("USA");
		merchpackOptObj.setSubCatPortOfOriginName("Texas");
		merchpackOptObj.setFineLineFactoryId("120");
		merchpackOptObj.setFineLineMaxNbrOfPacks(5);
		merchpackOptObj.setFineLineMaxUnitsPerPack(2);
		merchpackOptObj.setFineLineColorCombination("FineLine Color Combination");
		merchpackOptObj.setFineLineSinglePackInd(1);
		merchpackOptObj.setFineLineSupplierName("FineLine Vendor Name");
		merchpackOptObj.setFineLineOriginCountryName("USA");
		merchpackOptObj.setFineLinePortOfOriginName("Texas");

		merchantPackOptimizationlist.add(merchpackOptObj);

		Lvl3 lvl3= new Lvl3();
		Supplier supplier = new Supplier();
		supplier.setSupplierName("Vendor Name");
		List<Supplier> suppliers = List.of(supplier);

		ColorCombinationConstraints colorCombinationConstraints = new ColorCombinationConstraints();
		colorCombinationConstraints.setFactoryId("120, 121");
		colorCombinationConstraints.setSuppliers(suppliers);
		FinelineLevelConstraints finelineLevelConstraints = new FinelineLevelConstraints();
		finelineLevelConstraints.setMaxPacks(50);
		Constraints constraints = new Constraints();
		constraints.setColorCombinationConstraints(colorCombinationConstraints);
		constraints.setFinelineLevelConstraints(finelineLevelConstraints);
		lvl3.setConstraints(constraints);
		lvl3.setLvl3Nbr(25);
		lvl3.setLvl2Nbr(0);
		lvl3.setLvl1Nbr(0);
		lvl3.setLvl0Nbr(0);
		List<Lvl3> lvl3List = List.of(lvl3);
		PackOptimizationResponse response = new PackOptimizationResponse();
		response.setPlanId(362L);
		response.setChannel("Store");
		response.setLvl0Nbr(0);
		response.setLvl1Nbr(0);
		response.setLvl2Nbr(0);
		response.setLvl3List(lvl3List);

		when(packOptConstraintMapper.packOptDetails(anyList())).thenReturn(response);

		when(packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelId)).thenReturn(merchantPackOptimizationlist);
		packOptResponse = packOptimizationService.getPackOptDetails(362L, 1);

		assertNotNull(packOptResponse);
		assertEquals(362L, packOptResponse.getPlanId());

		assertEquals("120, 121", packOptResponse.getLvl3List().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
		assertEquals("Vendor Name", packOptResponse.getLvl3List().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getSupplierName());
		assertEquals(50, packOptResponse.getLvl3List().get(0).getConstraints().getFinelineLevelConstraints().getMaxPacks());
	}

	@Test
	void test_getPackOptConstraintDetails() {
		PackOptConstraintResponseDTO packOptConstraintResponseDTO = new PackOptConstraintResponseDTO();
		List<PackOptConstraintResponseDTO> packOptConstraintResponseDTOList = new ArrayList<>();

		packOptConstraintResponseDTO.setPlanId(362L);
		packOptConstraintResponseDTO.setLvl0Nbr(0);
		packOptConstraintResponseDTO.setLvl1Nbr(0);
		packOptConstraintResponseDTO.setLvl2Nbr(0);
		packOptConstraintResponseDTO.setLvl3Nbr(25);
		packOptConstraintResponseDTO.setLvl4Nbr(252);
		packOptConstraintResponseDTO.setFinelineNbr(2542);
		packOptConstraintResponseDTO.setChannelId(1);
		packOptConstraintResponseDTO.setStyleNbr("34_2816_2_19_2");
		packOptConstraintResponseDTO.setCcId("34_2956_1_18_1_BLUE SAPPHIRE");
		packOptConstraintResponseDTO.setCcSupplierName("NIKE");

		packOptConstraintResponseDTOList.add(packOptConstraintResponseDTO);

		Supplier supplier = new Supplier();
		supplier.setSupplierName("NIKE");
		List<Supplier> suppliers = List.of(supplier);

		ColorCombinationConstraints colorCombinationConstraint = new ColorCombinationConstraints();
		colorCombinationConstraint.setSuppliers(suppliers);

		Constraints constraint = new Constraints();
		constraint.setColorCombinationConstraints(colorCombinationConstraint);
		Style style = new Style();
		style.setConstraints(constraint);
		Fineline fineline = new Fineline();
		fineline.setStyles(Collections.singletonList(style));

		Lvl4 lvl4 = new Lvl4();
		lvl4.setFinelines(Collections.singletonList(fineline));

		Lvl3 lvl3= new Lvl3();
		lvl3.setLvl3Nbr(25);
		lvl3.setLvl2Nbr(0);
		lvl3.setLvl1Nbr(0);
		lvl3.setLvl0Nbr(0);
		lvl3.setLvl4List(Collections.singletonList(lvl4));
		List<Lvl3> lvl3List = List.of(lvl3);

		PackOptimizationResponse response = new PackOptimizationResponse();
		response.setPlanId(362L);
		response.setChannel("Store");
		response.setLvl0Nbr(0);
		response.setLvl1Nbr(0);
		response.setLvl2Nbr(0);
		response.setLvl3List(lvl3List);

		when(styleCcPackOptConsRepository
				.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(anyLong(), anyInt(), anyInt())).thenReturn(packOptConstraintResponseDTOList);

		when(packOptConstraintMapper.packOptDetails(anyList())).thenReturn(response);
		PackOptConstraintRequest request = new PackOptConstraintRequest();
		request.setPlanId(362L);
		request.setChannel("Store");
		request.setFinelineNbr(2542);
		packOptResponse = packOptimizationService.getPackOptConstraintDetails(request);

		assertNotNull(packOptResponse);
		assertEquals(362L, packOptResponse.getPlanId());
		assertEquals("NIKE", packOptResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getSupplierName());
	}

	@Test
	void testUpdateRunStatusCode()
	{
		Long planId = 234L;
		Integer finelineNbr = 46;
		Integer status = 10;
		packOptimizationService.updatePackOptServiceStatus(planId, finelineNbr, status);
		Mockito.verify(packOptimizationService,Mockito.times(1)).updatePackOptServiceStatus(planId, finelineNbr, status);
		assertEquals(46,finelineNbr);
	}	


	@Test
	void testGetPackOptFinelineDetails() {
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
				finelinePackOptimizationResponse, 483l, 1);
		when(finelinePackOptimizationRepository.getPackOptByFineline(483l, 5147))
				.thenReturn(finelinePackOptimizationResponseList);
		finelinePackOptimizationResponse = packOptimizationService.getPackOptFinelineDetails(483l, 5147, 1);
		Mockito.verify(packOptimizationMapper, Mockito.times(1)).mapPackOptimizationFineline(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any());
		assertNotNull(fineLinePackOptimizationResponse);
	}

    @Test
    void testAddColorCombination() {
        ColorCombinationRequest request = new ColorCombinationRequest(471L, "S3 2022", 50000, 34,
                6419, 12228, 31507, 2855, "Add", null,
                List.of(new ColorCombinationStyle("34_2855_4_19_8", List.of("34_2855_4_19_8_BLACK SOOT"))));

        CcPackOptimization ccPackOptimization = new CcPackOptimization();

        when(ccPackOptimizationRepository.findCCPackOptimizationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
                Mockito.anyList())).thenReturn(List.of(ccPackOptimization));
        when(ccPackOptimizationRepository.saveAll(Mockito.anyList())).thenReturn(List.of(ccPackOptimization));

        StatusResponse response = packOptimizationService.addColorCombination(request);

        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getStatus());
        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getMessage());
        assertEquals("0", ccPackOptimization.getColorCombination());

        ccPackOptimization.setColorCombination(null);
        when(ccPackOptimizationRepository.findCCPackOptimizationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
                Mockito.anyList())).thenReturn(Collections.emptyList());

        response = packOptimizationService.addColorCombination(request);
        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getStatus());
        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getMessage());
        assertNull(ccPackOptimization.getColorCombination());


    }

	@Test
	void testAddColorCombinationWithIncrement() {
		ColorCombinationRequest request = new ColorCombinationRequest(471L, "S3 2022", 50000, 34,
				6419, 12228, 31507, 2855, "Add", null,
				List.of(new ColorCombinationStyle("34_2855_4_19_8", List.of("34_2855_4_19_8_BLACK SOOT"))));
		CcPackOptimization ccPackOptimization = new CcPackOptimization();
		Set<String> colorCombinationSet = new HashSet<>();
		colorCombinationSet.add(null);
		colorCombinationSet.add("Test");
		colorCombinationSet.add("1");
		colorCombinationSet.add("5");
		colorCombinationSet.add("11");
		colorCombinationSet.add("19");
		when(ccPackOptimizationRepository.findCCPackOptimizationColorCombinationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(colorCombinationSet);

		when(ccPackOptimizationRepository.findCCPackOptimizationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
				Mockito.anyList())).thenReturn(List.of(ccPackOptimization));
		when(ccPackOptimizationRepository.saveAll(Mockito.anyList())).thenReturn(List.of(ccPackOptimization));

		StatusResponse response = packOptimizationService.addColorCombination(request);

		assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getStatus());
		assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getMessage());
		assertEquals("20", ccPackOptimization.getColorCombination());
	}

    @Test
    void testAddColorCombinationException() {
        ColorCombinationRequest request = new ColorCombinationRequest(471L, "S3 2022", 50000, 34,
                6419, 12228, 31507, 2855, "Add", null,
                List.of(new ColorCombinationStyle("34_2855_4_19_8", List.of("34_2855_4_19_8_BLACK SOOT"))));

        when(ccPackOptimizationRepository.findCCPackOptimizationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
                Mockito.anyList())).thenThrow(HibernateException.class);

        StatusResponse response = packOptimizationService.addColorCombination(request);

        assertEquals(SizeAndPackConstants.FAILED_STATUS, response.getStatus());
    }

    @Test
    void testAddColorCombinationExist() {
        ColorCombinationRequest request = new ColorCombinationRequest(471L, "S3 2022", 50000, 34,
                6419, 12228, 31507, 2855, "Add", null,
                List.of(new ColorCombinationStyle("34_2855_4_19_8", List.of("34_2855_4_19_8_BLACK SOOT"))));

        CcPackOptimization ccPackOptimization = new CcPackOptimization();
        ccPackOptimization.setColorCombination("0");

        when(ccPackOptimizationRepository.findCCPackOptimizationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
                Mockito.anyList())).thenReturn(List.of(ccPackOptimization));

        StatusResponse response = packOptimizationService.addColorCombination(request);

        assertEquals(SizeAndPackConstants.FAILED_STATUS, response.getStatus());
        assertEquals(SizeAndPackConstants.COLOR_COMBINATION_EXIST_MSG, response.getMessage());
    }

    @Test
    void testDeleteColorCombination() {
        ColorCombinationRequest request = new ColorCombinationRequest(471L, "S3 2022", 50000, 34,
                6419, 12228, 31507, 2855, "Delete", List.of("4-22-1"),
                List.of(new ColorCombinationStyle("34_2855_4_19_8", List.of("34_2855_4_19_8_BLACK SOOT"))));

        CcPackOptimization ccPackOptimization = new CcPackOptimization();
        ccPackOptimization.setColorCombination("4-22-1");

        when(ccPackOptimizationRepository.findCCPackOptimizationByColorCombinationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList())).thenReturn(List.of(ccPackOptimization));
        when(ccPackOptimizationRepository.saveAll(Mockito.anyList())).thenReturn(List.of(ccPackOptimization));

        StatusResponse response = packOptimizationService.deleteColorCombination(request);

        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getStatus());
        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getMessage());
        assertNull(ccPackOptimization.getColorCombination());

        ccPackOptimization.setColorCombination("4-22-1");
        when(ccPackOptimizationRepository.findCCPackOptimizationByColorCombinationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList())).thenReturn(Collections.emptyList());

        response = packOptimizationService.deleteColorCombination(request);

        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getStatus());
        assertEquals(SizeAndPackConstants.SUCCESS_STATUS, response.getMessage());
        assertNotNull(ccPackOptimization.getColorCombination());
    }

    @Test
    void testUpdateColorCombinationDeleteNull() {
        ColorCombinationRequest request = new ColorCombinationRequest(471L, "S3 2022", 50000, 34,
                6419, 12228, 31507, 2855, "Delete", null,
                List.of(new ColorCombinationStyle("34_2855_4_19_8", List.of("34_2855_4_19_8_BLACK SOOT"))));

        StatusResponse response = packOptimizationService.deleteColorCombination(request);

        assertEquals(SizeAndPackConstants.FAILED_STATUS, response.getStatus());
        assertEquals(SizeAndPackConstants.COLOR_COMBINATION_MISSING_MSG, response.getMessage());

        request.setColorCombinationIds(Collections.emptyList());
        response = packOptimizationService.deleteColorCombination(request);

        assertEquals(SizeAndPackConstants.FAILED_STATUS, response.getStatus());
        assertEquals(SizeAndPackConstants.COLOR_COMBINATION_MISSING_MSG, response.getMessage());
    }

    @Test
    void testDeleteColorCombinationException() {
        ColorCombinationRequest request = new ColorCombinationRequest(471L, "S3 2022", 50000, 34,
                6419, 12228, 31507, 2855, "Add", List.of("4-22-1"),
                List.of(new ColorCombinationStyle("34_2855_4_19_8", List.of("34_2855_4_19_8_BLACK SOOT"))));

        when(ccPackOptimizationRepository.findCCPackOptimizationByColorCombinationList(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList())).thenThrow(HibernateException.class);

        StatusResponse response = packOptimizationService.deleteColorCombination(request);

        assertEquals(SizeAndPackConstants.FAILED_STATUS, response.getStatus());
    }





}
