package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.RunStatusText;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import com.walmart.aex.sp.repository.FineLinePackOptimizationRepository;
import com.walmart.aex.sp.repository.FinelinePackOptRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.repository.StyleCcPackOptConsRepository;
import com.walmart.aex.sp.util.CommonGCPUtil;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.MULTI_BUMP_PACK_SUFFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackOptimizationServiceTest {

    @InjectMocks
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
    @Mock
    private IntegrationHubService integrationHubService;

    @Mock
    private IntegrationHubServiceProperties integrationHubServiceProperties;
    @Mock
    private SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    @Mock
    private CommonGCPUtil commonGCPUtil;
    @Captor
    private ArgumentCaptor<Set<AnalyticsMlSend>> analyticsMlSendRepoDataCaptor;

    @Captor
    private ArgumentCaptor<AnalyticsMlSend> analyticsMlSendArgumentCaptor;

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
        merchpackOptObj.setMerchPortOfOriginName("Texas");
        merchpackOptObj.setSubCatFactoryId("120");
        merchpackOptObj.setSubCatMaxNbrOfPacks(20);
        merchpackOptObj.setSubCatMaxUnitsPerPack(10);
        merchpackOptObj.setSubCatColorCombination("Sub Category Color Combination");
        merchpackOptObj.setSubCatSinglePackInd(1);
        merchpackOptObj.setSubCatSupplierName("Supplier Vendor Name");
        merchpackOptObj.setSubCatPortOfOriginName("Texas");
        merchpackOptObj.setFineLineFactoryId("120");
        merchpackOptObj.setFineLineMaxNbrOfPacks(5);
        merchpackOptObj.setFineLineMaxUnitsPerPack(2);
        merchpackOptObj.setFineLineColorCombination("FineLine Color Combination");
        merchpackOptObj.setFineLineSinglePackInd(1);
        merchpackOptObj.setFineLineSupplierName("FineLine Vendor Name");
        merchpackOptObj.setFineLinePortOfOriginName("Texas");

        merchantPackOptimizationlist.add(merchpackOptObj);

        Lvl3 lvl3 = new Lvl3();
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
        style.setStyleNbr("34_2816_2_19_2");
        style.setAltStyleDesc("Test_34_2816_2_19_2");
        style.setConstraints(constraint);
        CustomerChoice cc = new CustomerChoice();
        cc.setCcId("34_2956_1_18_1_BLUE SAPPHIRE");
        cc.setAltCcDesc("34_2956_1_18_1_BLUETest");
        style.setCustomerChoices(Collections.singletonList(cc));
        Fineline fineline = new Fineline();
        fineline.setStyles(Collections.singletonList(style));

        Lvl4 lvl4 = new Lvl4();
        lvl4.setFinelines(Collections.singletonList(fineline));

        Lvl3 lvl3 = new Lvl3();
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
        assertEquals("Test_34_2816_2_19_2", packOptResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getAltStyleDesc());
        assertEquals("34_2956_1_18_1_BLUETest", packOptResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getAltCcDesc());
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
                finelinePackOptimizationResponse, 483l, 1,1);
        when(finelinePackOptimizationRepository.getPackOptByFineline(483l, 5147))
                .thenReturn(finelinePackOptimizationResponseList);
        finelinePackOptimizationResponse = packOptimizationService.getPackOptFinelineDetails(483l, 5147, 1);
        Mockito.verify(packOptimizationMapper, Mockito.times(1)).mapPackOptimizationFineline(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any());
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


    @Test
    void test_callIntegrationHubForPackOptByFinelineShouldSaveParentAndChildRecords() throws IllegalAccessException {

        RunPackOptRequest request = getRunPackOptRequestAndMockCalls();
        packOptimizationService.callIntegrationHubForPackOptByFineline(request);

        verify(analyticsMlSendRepository, times(1)).saveAll(analyticsMlSendRepoDataCaptor.capture());

        assertEquals(2, analyticsMlSendRepoDataCaptor.getValue().size());
        LinkedList<Integer> actualFineLines = analyticsMlSendRepoDataCaptor.getValue().stream().map(AnalyticsMlSend::getFinelineNbr).collect(Collectors.toCollection(LinkedList::new));
        assertTrue(actualFineLines.containsAll(List.of(2829, 2819)));

        for (AnalyticsMlSend analyticsMlSend : analyticsMlSendRepoDataCaptor.getValue()) {
            if (analyticsMlSend.getFinelineNbr() == 2829) {
                assertEquals(1, analyticsMlSend.getAnalyticsMlChildSend().size());
                assertEquals(1, analyticsMlSend.getAnalyticsMlChildSend().iterator().next().getBumpPackNbr());
                assertEquals("{\"context\":{\"getPackOptFinelineDetails\":\"testBaseUrl/api/packOptimization/plan/{planId}/fineline/{finelineNbr}\",\"updatePackOptFinelineStatus\":\"testBaseUrl/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/status/{status}\",\"planId\":12,\"finelineNbrs\":[\"2829\"],\"env\":null}}", analyticsMlSend.getAnalyticsMlChildSend().iterator().next().getPayloadObj());
            } else {
                assertEquals(2, analyticsMlSend.getAnalyticsMlChildSend().size());
                assertTrue(analyticsMlSend.getAnalyticsMlChildSend().stream().map(AnalyticsMlChildSend::getBumpPackNbr).collect(Collectors.toList()).containsAll(List.of(1, 2)));
                Set<AnalyticsMlChildSend> analyticsMlChildSendSet = analyticsMlSend.getAnalyticsMlChildSend();
                for (AnalyticsMlChildSend analyticsMlChildSend : analyticsMlChildSendSet) {
                    if (analyticsMlChildSend.getBumpPackNbr() == 2) {
                        assertEquals("{\"context\":{\"getPackOptFinelineDetails\":\"testBaseUrl/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/bumppack/{bumpPackNbr}\",\"updatePackOptFinelineStatus\":\"testBaseUrl/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/status/{status}\",\"planId\":12,\"finelineNbrs\":[\"2819-BP2\"],\"env\":null}}", analyticsMlChildSend.getPayloadObj());
                        assertEquals("133333", analyticsMlChildSend.getAnalyticsJobId());
                    } else if (analyticsMlChildSend.getBumpPackNbr() == 1) {
                        assertEquals("{\"context\":{\"getPackOptFinelineDetails\":\"testBaseUrl/api/packOptimization/plan/{planId}/fineline/{finelineNbr}\",\"updatePackOptFinelineStatus\":\"testBaseUrl/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/status/{status}\",\"planId\":12,\"finelineNbrs\":[\"2819\"],\"env\":null}}", analyticsMlChildSend.getPayloadObj());
                        assertEquals("122222", analyticsMlChildSend.getAnalyticsJobId());
                    }
                }
            }
        }
    }

    @Test
    void test_callIntegrationHubForPackOptByFinelineShouldSave1ChildIfBumpCountIs0() throws IllegalAccessException {

        RunPackOptRequest request = getRunPackOptRequestAndMockCallsForZeroBumpPack();
        packOptimizationService.callIntegrationHubForPackOptByFineline(request);

        verify(analyticsMlSendRepository, times(1)).saveAll(analyticsMlSendRepoDataCaptor.capture());

        assertEquals(1, analyticsMlSendRepoDataCaptor.getValue().size());
        LinkedList<Integer> actualFineLines = analyticsMlSendRepoDataCaptor.getValue().stream().map(AnalyticsMlSend::getFinelineNbr).collect(Collectors.toCollection(LinkedList::new));
        assertTrue(actualFineLines.contains(2819));

        for (AnalyticsMlSend analyticsMlSend : analyticsMlSendRepoDataCaptor.getValue()) {
            assertEquals(1, analyticsMlSend.getAnalyticsMlChildSend().size());
            assertEquals(1, analyticsMlSend.getAnalyticsMlChildSend().iterator().next().getBumpPackNbr());
        }
    }

    private RunPackOptRequest getRunPackOptRequestAndMockCalls() throws IllegalAccessException {
        mockUrlForIntegrationHubProperties();

        InputRequest inputRequest = new InputRequest();
        List<Lvl3Dto> lvl3List = new ArrayList<>();
        Lvl3Dto lvl3Dto = new Lvl3Dto();
        List<Lvl4Dto> lvl4List = new ArrayList<>();
        Lvl4Dto lvl4Dto = new Lvl4Dto();

        FinelineDto finelineDto1 = new FinelineDto();
        finelineDto1.setFinelineNbr(2819);
        FinelineDto finelineDto2 = new FinelineDto();
        finelineDto2.setFinelineNbr(2829);

        List<FinelineDto> fineLines = new ArrayList<>(Arrays.asList(finelineDto1, finelineDto2));

        lvl4Dto.setFinelines(fineLines);
        lvl4List.add(lvl4Dto);
        lvl3Dto.setLvl4List(lvl4List);
        lvl3List.add(lvl3Dto);
        inputRequest.setLvl3List(lvl3List);

        RunPackOptRequest request = new RunPackOptRequest();
        request.setPlanId(12L);
        request.setInputRequest(inputRequest);
        request.setRunUser("RandomUser");

        IntegrationHubResponseDTO integrationHubResponseDto1 = new IntegrationHubResponseDTO();
        integrationHubResponseDto1.setJobId("1234455");
        integrationHubResponseDto1.setWf_running_id("122222");

        IntegrationHubResponseDTO integrationHubResponseDto2 = new IntegrationHubResponseDTO();
        integrationHubResponseDto2.setJobId("1234566");
        integrationHubResponseDto2.setWf_running_id("133333");

        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO();
        buyQntyResponseDTO1.setBumpPackCnt(2);
        buyQntyResponseDTO1.setFinelineNbr(2819);
        BuyQntyResponseDTO buyQntyResponseDTO2 = new BuyQntyResponseDTO();
        buyQntyResponseDTO2.setBumpPackCnt(1);
        buyQntyResponseDTO2.setFinelineNbr(2829);
        List<BuyQntyResponseDTO> bumpPackCntByFinelines = new ArrayList<>(Arrays.asList(buyQntyResponseDTO1, buyQntyResponseDTO2));
        when(commonGCPUtil.delete(anyString(), anyString())).thenReturn(false);
        when(integrationHubService.callIntegrationHubForPackOpt(any())).thenReturn(integrationHubResponseDto1).thenReturn(integrationHubResponseDto2);
        when(spFineLineChannelFixtureRepository.getBumpPackCntByFinelines(anyLong(), anyList())).thenReturn(bumpPackCntByFinelines);
        return request;
    }

    private RunPackOptRequest getRunPackOptRequestAndMockCallsForZeroBumpPack() throws IllegalAccessException {
        mockUrlForIntegrationHubProperties();

        InputRequest inputRequest = new InputRequest();
        List<Lvl3Dto> lvl3List = new ArrayList<>();
        Lvl3Dto lvl3Dto = new Lvl3Dto();
        List<Lvl4Dto> lvl4List = new ArrayList<>();
        Lvl4Dto lvl4Dto = new Lvl4Dto();

        FinelineDto finelineDto1 = new FinelineDto();
        finelineDto1.setFinelineNbr(2819);

        List<FinelineDto> fineLines = new ArrayList<>(List.of(finelineDto1));

        lvl4Dto.setFinelines(fineLines);
        lvl4List.add(lvl4Dto);
        lvl3Dto.setLvl4List(lvl4List);
        lvl3List.add(lvl3Dto);
        inputRequest.setLvl3List(lvl3List);

        RunPackOptRequest request = new RunPackOptRequest();
        request.setPlanId(12L);
        request.setInputRequest(inputRequest);
        request.setRunUser("RandomUser");

        IntegrationHubResponseDTO integrationHubResponseDto = new IntegrationHubResponseDTO();
        integrationHubResponseDto.setJobId("1234455");
        integrationHubResponseDto.setWf_running_id("122222");

        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO();
        buyQntyResponseDTO1.setBumpPackCnt(0);
        buyQntyResponseDTO1.setFinelineNbr(2819);

        List<BuyQntyResponseDTO> bumpPackCntByFinelines = new ArrayList<>(List.of(buyQntyResponseDTO1));
        when(commonGCPUtil.delete(anyString(), anyString())).thenReturn(false);
        when(integrationHubService.callIntegrationHubForPackOpt(any())).thenReturn(integrationHubResponseDto);
        when(spFineLineChannelFixtureRepository.getBumpPackCntByFinelines(anyLong(), anyList())).thenReturn(bumpPackCntByFinelines);
        return request;
    }

    private void mockUrlForIntegrationHubProperties() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(PackOptimizationService.class, "integrationHubServiceProperties");
        field.setAccessible(true);
        field.set(packOptimizationService, integrationHubServiceProperties);
        when(integrationHubServiceProperties.getSizeAndPackUrl()).thenReturn("testBaseUrl");
    }

    @Test
    void test_updatePackOptServiceStatusShouldUpdateRunStatusCodeForBumpPack1() {
        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
        AnalyticsMlChildSend analyticsMlChildSend1 = new AnalyticsMlChildSend();
        analyticsMlChildSend1.setRunStatusCode(3);
        analyticsMlChildSend1.setBumpPackNbr(1);
        AnalyticsMlChildSend analyticsMlChildSend2 = new AnalyticsMlChildSend();
        analyticsMlChildSend2.setRunStatusCode(3);
        analyticsMlChildSend2.setBumpPackNbr(2);
        Set<AnalyticsMlChildSend> analyticsMlChildSendList = new HashSet<>(Arrays.asList(analyticsMlChildSend1, analyticsMlChildSend2));
        analyticsMlSend.setAnalyticsMlChildSend(analyticsMlChildSendList);
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.of(analyticsMlSend));
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(1)).save(analyticsMlSendArgumentCaptor.capture());
        Set<AnalyticsMlChildSend> actualAnalyticsMlChildSendList = analyticsMlSendArgumentCaptor.getValue().getAnalyticsMlChildSend();
        assertEquals(2, actualAnalyticsMlChildSendList.size());
        for (AnalyticsMlChildSend analyticsMlChildSend : actualAnalyticsMlChildSendList) {
            if (analyticsMlChildSend.getBumpPackNbr() == 1) {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            } else {
                assertEquals(3, analyticsMlChildSend.getRunStatusCode());
            }
        }
    }

    @Test
    void test_updatePackOptServiceStatusShouldThrowExceptionIfDbReturnMultipleRecords() {
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenThrow(RuntimeException.class);
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(0)).save(any());
    }

    @Test
    void test_updatePackOptServiceStatusShouldNotSaveAnyRecordIfDbReturnEmptyData() {
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(0)).save(any());
    }

    @Test
    void test_updatePackOptServiceStatusShouldSkipExecutionIfFineLineIsNotCorrect() {
        packOptimizationService.updatePackOptServiceStatus(12L, "TEST", RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(0)).save(any());
    }

    @Test
    void test_updatePackOptServiceStatusShouldUpdateParentRunStatusToCompletedWhenAllChildStatusIsCompleted() {
        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
        AnalyticsMlChildSend analyticsMlChildSend1 = new AnalyticsMlChildSend();
        analyticsMlChildSend1.setRunStatusCode(3);
        analyticsMlChildSend1.setBumpPackNbr(1);
        AnalyticsMlChildSend analyticsMlChildSend2 = new AnalyticsMlChildSend();
        analyticsMlChildSend2.setRunStatusCode(6);
        analyticsMlChildSend2.setBumpPackNbr(2);
        Set<AnalyticsMlChildSend> analyticsMlChildSendList = new HashSet<>(Arrays.asList(analyticsMlChildSend1, analyticsMlChildSend2));
        analyticsMlSend.setAnalyticsMlChildSend(analyticsMlChildSendList);
        analyticsMlSend.setRunStatusCode(3);
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.of(analyticsMlSend));
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(1)).save(analyticsMlSendArgumentCaptor.capture());
        Set<AnalyticsMlChildSend> actualAnalyticsMlChildSendList = analyticsMlSendArgumentCaptor.getValue().getAnalyticsMlChildSend();
        assertEquals(2, actualAnalyticsMlChildSendList.size());
        assertEquals(6, analyticsMlSendArgumentCaptor.getValue().getRunStatusCode());
        for (AnalyticsMlChildSend analyticsMlChildSend : actualAnalyticsMlChildSendList) {
            if (analyticsMlChildSend.getBumpPackNbr() == 1) {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            } else {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            }
        }
    }

    @Test
    void test_updatePackOptServiceStatusShouldUpdateParentRunStatusToErrorWhenAtleastOneChildStatusIsErrorAndOthersAreCompleted() {
        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
        AnalyticsMlChildSend analyticsMlChildSend1 = new AnalyticsMlChildSend();
        analyticsMlChildSend1.setRunStatusCode(3);
        analyticsMlChildSend1.setBumpPackNbr(1);
        AnalyticsMlChildSend analyticsMlChildSend2 = new AnalyticsMlChildSend();
        analyticsMlChildSend2.setRunStatusCode(100);
        analyticsMlChildSend2.setBumpPackNbr(2);
        AnalyticsMlChildSend analyticsMlChildSend3 = new AnalyticsMlChildSend();
        analyticsMlChildSend3.setRunStatusCode(6);
        analyticsMlChildSend3.setBumpPackNbr(3);
        Set<AnalyticsMlChildSend> analyticsMlChildSendList = new HashSet<>(Arrays.asList(analyticsMlChildSend1, analyticsMlChildSend2, analyticsMlChildSend3));
        analyticsMlSend.setAnalyticsMlChildSend(analyticsMlChildSendList);
        analyticsMlSend.setRunStatusCode(3);
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.of(analyticsMlSend));
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(1)).save(analyticsMlSendArgumentCaptor.capture());
        Set<AnalyticsMlChildSend> actualAnalyticsMlChildSendList = analyticsMlSendArgumentCaptor.getValue().getAnalyticsMlChildSend();
        assertEquals(3, actualAnalyticsMlChildSendList.size());
        assertEquals(101, analyticsMlSendArgumentCaptor.getValue().getRunStatusCode());
        for (AnalyticsMlChildSend analyticsMlChildSend : actualAnalyticsMlChildSendList) {
            if (analyticsMlChildSend.getBumpPackNbr() == 1) {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            } else if (analyticsMlChildSend.getBumpPackNbr() == 2) {
                assertEquals(100, analyticsMlChildSend.getRunStatusCode());
            } else {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            }
        }
    }

    @Test
    void test_updatePackOptServiceStatusShouldUpdateParentRunStatusToSentWhenAtleastOneChildStatusIsSent() {
        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
        AnalyticsMlChildSend analyticsMlChildSend1 = new AnalyticsMlChildSend();
        analyticsMlChildSend1.setRunStatusCode(3);
        analyticsMlChildSend1.setBumpPackNbr(1);
        AnalyticsMlChildSend analyticsMlChildSend2 = new AnalyticsMlChildSend();
        analyticsMlChildSend2.setRunStatusCode(6);
        analyticsMlChildSend2.setBumpPackNbr(2);
        AnalyticsMlChildSend analyticsMlChildSend3 = new AnalyticsMlChildSend();
        analyticsMlChildSend3.setRunStatusCode(3);
        analyticsMlChildSend3.setBumpPackNbr(3);
        Set<AnalyticsMlChildSend> analyticsMlChildSendList = new HashSet<>(Arrays.asList(analyticsMlChildSend1, analyticsMlChildSend2, analyticsMlChildSend3));
        analyticsMlSend.setAnalyticsMlChildSend(analyticsMlChildSendList);
        analyticsMlSend.setRunStatusCode(3);
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.of(analyticsMlSend));
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(1)).save(analyticsMlSendArgumentCaptor.capture());
        Set<AnalyticsMlChildSend> actualAnalyticsMlChildSendList = analyticsMlSendArgumentCaptor.getValue().getAnalyticsMlChildSend();
        assertEquals(3, actualAnalyticsMlChildSendList.size());
        assertEquals(3, analyticsMlSendArgumentCaptor.getValue().getRunStatusCode());
        for (AnalyticsMlChildSend analyticsMlChildSend : actualAnalyticsMlChildSendList) {
            if (analyticsMlChildSend.getBumpPackNbr() == 1) {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            } else if (analyticsMlChildSend.getBumpPackNbr() == 2) {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            } else {
                assertEquals(3, analyticsMlChildSend.getRunStatusCode());
            }
        }
    }

    @Test
    void test_updatePackOptServiceStatusShouldUpdateParentRunStatusToSENT_TO_ANALYTICSWhenAllChildStatusIsNotCompleted() {
        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
        AnalyticsMlChildSend analyticsMlChildSend1 = new AnalyticsMlChildSend();
        analyticsMlChildSend1.setRunStatusCode(3);
        analyticsMlChildSend1.setBumpPackNbr(1);
        AnalyticsMlChildSend analyticsMlChildSend2 = new AnalyticsMlChildSend();
        analyticsMlChildSend2.setRunStatusCode(3);
        analyticsMlChildSend2.setBumpPackNbr(2);
        Set<AnalyticsMlChildSend> analyticsMlChildSendList = new HashSet<>(Arrays.asList(analyticsMlChildSend1, analyticsMlChildSend2));
        analyticsMlSend.setAnalyticsMlChildSend(analyticsMlChildSendList);
        analyticsMlSend.setRunStatusCode(3);
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.of(analyticsMlSend));
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(1)).save(analyticsMlSendArgumentCaptor.capture());
        Set<AnalyticsMlChildSend> actualAnalyticsMlChildSendList = analyticsMlSendArgumentCaptor.getValue().getAnalyticsMlChildSend();
        assertEquals(2, actualAnalyticsMlChildSendList.size());
        assertEquals(3, analyticsMlSendArgumentCaptor.getValue().getRunStatusCode());
        for (AnalyticsMlChildSend analyticsMlChildSend : actualAnalyticsMlChildSendList) {
            if (analyticsMlChildSend.getBumpPackNbr() == 1) {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            } else {
                assertEquals(3, analyticsMlChildSend.getRunStatusCode());
            }
        }
    }

    @Test
    void test_updatePackOptServiceStatusShouldUpdateParentRunStatusToANALYTICS_ERRORWhenAllChildOrAChildStatusIsANALYTICS_ERROR() {
        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
        AnalyticsMlChildSend analyticsMlChildSend1 = new AnalyticsMlChildSend();
        analyticsMlChildSend1.setRunStatusCode(3);
        analyticsMlChildSend1.setBumpPackNbr(1);
        AnalyticsMlChildSend analyticsMlChildSend2 = new AnalyticsMlChildSend();
        analyticsMlChildSend2.setRunStatusCode(10);
        analyticsMlChildSend2.setBumpPackNbr(2);
        AnalyticsMlChildSend analyticsMlChildSend3 = new AnalyticsMlChildSend();
        analyticsMlChildSend3.setRunStatusCode(6);
        analyticsMlChildSend3.setBumpPackNbr(3);
        Set<AnalyticsMlChildSend> analyticsMlChildSendList = new HashSet<>(Arrays.asList(analyticsMlChildSend1, analyticsMlChildSend2, analyticsMlChildSend3));
        analyticsMlSend.setAnalyticsMlChildSend(analyticsMlChildSendList);
        analyticsMlSend.setRunStatusCode(3);
        when(analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.of(analyticsMlSend));
        packOptimizationService.updatePackOptServiceStatus(12L, "2828" + MULTI_BUMP_PACK_SUFFIX + 1, RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        verify(analyticsMlSendRepository, times(1)).save(analyticsMlSendArgumentCaptor.capture());
        Set<AnalyticsMlChildSend> actualAnalyticsMlChildSendList = analyticsMlSendArgumentCaptor.getValue().getAnalyticsMlChildSend();
        assertEquals(3, actualAnalyticsMlChildSendList.size());
        assertEquals(RunStatusCodeType.ERROR.getId(), analyticsMlSendArgumentCaptor.getValue().getRunStatusCode());
        for (AnalyticsMlChildSend analyticsMlChildSend : actualAnalyticsMlChildSendList) {
            if (analyticsMlChildSend.getBumpPackNbr() == 1 || analyticsMlChildSend.getBumpPackNbr() == 3) {
                assertEquals(6, analyticsMlChildSend.getRunStatusCode());
            } else {
                assertEquals(10, analyticsMlChildSend.getRunStatusCode());
            }
        }
    }

    @Test
    void test_getPackOptFinelinesByStatusWhenStatusCodeIsValid() {
        List<AnalyticsMlSend> analyticsMlSendList = new ArrayList<>();
        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
        analyticsMlSend.setPlanId(12l);
        analyticsMlSend.setFinelineNbr(1234);
        analyticsMlSend.setRunStatusCode(3);
        analyticsMlSend.setStartTs(new Date());
        analyticsMlSend.setEndTs(new Date());
        RunStatusText text = new RunStatusText();
        text.setRunStatusCode(3);
        text.setRunStatusDesc("SENT");
        analyticsMlSend.setRunStatusText(text);
        analyticsMlSendList.add(analyticsMlSend);
        when(analyticsMlSendRepository.getAllFinelinesByStatus(anyList()))
                .thenReturn(analyticsMlSendList);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(3);
        List<PackOptFinelinesByStatusResponse> finelines = packOptimizationService.getPackOptFinelinesByStatus(statusList);
        assertEquals(1,finelines.size());
    }

    @Test
    void test_getPackOptFinelinesByStatusWhenStatusCodeIsInValid() {
        when(analyticsMlSendRepository.getAllFinelinesByStatus(anyList()))
                .thenReturn(new ArrayList<>());
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        List<PackOptFinelinesByStatusResponse> finelines = packOptimizationService.getPackOptFinelinesByStatus(statusList);
        assertEquals(0,finelines.size());
    }

}
