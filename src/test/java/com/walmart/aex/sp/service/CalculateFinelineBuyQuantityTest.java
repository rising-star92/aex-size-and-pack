package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpStyleChannelFixture;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.repository.StyleReplenishmentRepository;
import com.walmart.aex.sp.service.impl.DeptAdminRuleServiceImpl;
import com.walmart.aex.sp.util.BQFPResponseInputs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Slf4j
class CalculateFinelineBuyQuantityTest {

    @InjectMocks
    CalculateFinelineBuyQuantity calculateFinelineBuyQuantity;

    @Mock
    private SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    @Mock
    BQFPService bqfpService;

    @Spy
    AddStoreBuyQuantityService addStoreBuyQuantityService;

    @Mock
    CalculateInitialSetQuantityService  calculateInitialSetQuantityService;

    @Mock
    CalculateBumpPackQtyService calculateBumpPackQtyService;

    @Mock
    BuyQuantityConstraintService buyQuantityConstraintService;

    @Mock
    private StyleReplenishmentRepository styleReplenishmentRepository;

    @Mock
    private FineLineReplenishmentRepository fineLineReplenishmentRepository;

    @InjectMocks
    CalculateOnlineFinelineBuyQuantity calculateOnlineFinelineBuyQuantity;

    @Mock
    StrategyFetchService strategyFetchService;

    @Mock
    DeptAdminRuleServiceImpl deptAdminRuleService;

    ReplenishmentsOptimizationService replenishmentsOptimizationServices;

   @Spy
   ObjectMapper mapper = new ObjectMapper();

    @BeforeEach

    void setUp() {
       MockitoAnnotations.openMocks(this);
       calculateInitialSetQuantityService = new CalculateInitialSetQuantityService();
       calculateBumpPackQtyService = new CalculateBumpPackQtyService();
       buyQuantityConstraintService = new BuyQuantityConstraintService(calculateBumpPackQtyService);
       addStoreBuyQuantityService = new AddStoreBuyQuantityService(mapper, calculateBumpPackQtyService, buyQuantityConstraintService, calculateInitialSetQuantityService);
       replenishmentsOptimizationServices=new ReplenishmentsOptimizationService();

       calculateOnlineFinelineBuyQuantity = new  CalculateOnlineFinelineBuyQuantity (mapper, new BuyQtyReplenishmentMapperService(),replenishmentsOptimizationServices );
       calculateFinelineBuyQuantity = new CalculateFinelineBuyQuantity(bqfpService, mapper, new BuyQtyReplenishmentMapperService(), calculateOnlineFinelineBuyQuantity,
               strategyFetchService,addStoreBuyQuantityService, buyQuantityConstraintService, deptAdminRuleService);
    }

    @Test
    void initialSetCalculationTest() throws SizeAndPackException, IOException {
       final String path = "/plan72fineline1500";
       BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
       APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
       BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
       when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
       when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
       when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
       when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
       CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9071, 7205, 1500, 12L);
       CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(1)).findFirst().get();
        Set<SpCustomerChoiceChannelFixtureSize> fixture1Sizes = fixture1
                .getSpStyleChannelFixtures().stream().findFirst()
                .get().getSpCustomerChoiceChannelFixture().stream().findFirst()
                .get().getSpCustomerChoiceChannelFixtureSize();

        assertEquals(6, fixture1Sizes.size(), "Fixture 1 Should have 6 sizes present");
        int fix1xs = 6593;
        int fix1s = 17556;
        int fix1m = 30790;
        int fix1l = 32604;
        int fix1xl = 21083;
        int fix1xxl = 11896;
        int expectedTotalFix1InitialSetQty = IntStream.of(fix1xs, fix1s, fix1m, fix1l, fix1xl, fix1xxl).sum();
        assertUnitValueBySize(fixture1Sizes, "XS", fix1xs, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "S", fix1s, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "M", fix1m, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "L", fix1l, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XL", fix1xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XXL", fix1xxl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertEquals(expectedTotalFix1InitialSetQty, (int) fixture1.getInitialSetQty(), "Fixture 1 Initial Set Qty rollup should be sum of all size values");
    }

    @Test
    void replenishmentCalculationTest() throws IOException, SizeAndPackException {
        final String path = "/plan72fineline4440";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9074, 7207, 4440, 12L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        Set<SpCustomerChoiceChannelFixtureSize> spCcChanFixSizes = new HashSet<>(response.getSpFineLineChannelFixtures().get(1)
                .getSpStyleChannelFixtures().stream().findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().findFirst().get()
                .getSpCustomerChoiceChannelFixtureSize());

        assertNotNull(response.getMerchCatgReplPacks());
        assertEquals(1, response.getMerchCatgReplPacks().size(), "Only 1 merch catg repl pack created");
        assertEquals((Integer)18417,
              response.getMerchCatgReplPacks().get(0).getReplUnits(), "Repln units should be 19143 for cc");

    }

    @Test
    void bumpSetCalculationTest() throws IOException, SizeAndPackException {
        final String path = "/plan72fineline4440";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponseWithBumpSet"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9074, 7207, 4440, 12L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);
        SpFineLineChannelFixture spflChFix = response.getSpFineLineChannelFixtures().get(1);
        int expectedTotalBumpPackQty = 17361;
        assertEquals(expectedTotalBumpPackQty, (long) spflChFix.getBumpPackQty());

        SpStyleChannelFixture spStlChFix = spflChFix.getSpStyleChannelFixtures().stream().findFirst().get();
        assertEquals(expectedTotalBumpPackQty, (long) spStlChFix.getBumpPackQty());

        SpCustomerChoiceChannelFixture spCCChFix = spStlChFix.getSpCustomerChoiceChannelFixture().stream().findFirst().get();
        assertEquals(expectedTotalBumpPackQty, (long) spCCChFix.getBumpPackQty());

        Set<SpCustomerChoiceChannelFixtureSize> spCCFixSizes = spCCChFix.getSpCustomerChoiceChannelFixtureSize();

        assertEquals(7, spCCFixSizes.size(), "Should have 7 sizes");
        int fix1xs = 786;
        int fix1s = 2004;
        int fix1m = 3544;
        int fix1l = 4659;
        int fix1xl = 3249;
        int fix1xxl = 2160;
        int fix1xxxl = 959;
        int expectedTotalBumpPackQtySize = IntStream.of(fix1xs, fix1s, fix1m, fix1l, fix1xl, fix1xxl, fix1xxxl).sum();
        assertUnitValueBySize(spCCFixSizes, "XS", fix1xs, SpCustomerChoiceChannelFixtureSize::getBumpPackQty);
        assertUnitValueBySize(spCCFixSizes, "S", fix1s, SpCustomerChoiceChannelFixtureSize::getBumpPackQty);
        assertUnitValueBySize(spCCFixSizes, "M", fix1m, SpCustomerChoiceChannelFixtureSize::getBumpPackQty);
        assertUnitValueBySize(spCCFixSizes, "L", fix1l, SpCustomerChoiceChannelFixtureSize::getBumpPackQty);
        assertUnitValueBySize(spCCFixSizes, "XL", fix1xl, SpCustomerChoiceChannelFixtureSize::getBumpPackQty);
        assertUnitValueBySize(spCCFixSizes, "XXL", fix1xxl, SpCustomerChoiceChannelFixtureSize::getBumpPackQty);
        assertUnitValueBySize(spCCFixSizes, "XXXL", fix1xxxl, SpCustomerChoiceChannelFixtureSize::getBumpPackQty);

        long actualTotalBumpPackQty = spCCFixSizes.stream().mapToLong(SpCustomerChoiceChannelFixtureSize::getBumpPackQty).sum();
        assertEquals(expectedTotalBumpPackQtySize, actualTotalBumpPackQty, "Total of all size bump pack qtys should match");
        assertBumpSetStoreObject(spCCFixSizes, expectedTotalBumpPackQtySize);
    }

    @Test
    void strategyVolumeDeviationLevelCategoryTest() throws SizeAndPackException, IOException {
        final String path = "/plan72fineline1500";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/StrategyVolumeDeviationCategoryResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9071, 7205, 1500, 12L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);
        assertEquals(bqfpResponse.getVolumeDeviationStrategyLevelSelection(), BigDecimal.valueOf(3));
    }

    @Test
    void strategyVolumeDeviationLevelSubCategoryTest() throws SizeAndPackException, IOException {
        final String path = "/plan72fineline1500";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/StrategyVolumeDeviationSubCategoryResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9071, 7205, 1500, 12L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);
        assertEquals(bqfpResponse.getVolumeDeviationStrategyLevelSelection(), BigDecimal.valueOf(2));
    }

    @Test
    void strategyVolumeDeviationLevelFinelineTest() throws SizeAndPackException, IOException {
        final String path = "/plan72fineline1500";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/StrategyVolumeDeviationFinelineResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9071, 7205, 1500, 12L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);
        assertEquals(bqfpResponse.getVolumeDeviationStrategyLevelSelection(), BigDecimal.valueOf(1));
    }

    @Test
    void minReplenishmentRuleWithoutInitialSet() throws SizeAndPackException, IOException {
        BQFPResponse bqfpResponse = mapper.readValue("{\"planId\":236,\"lvl0Nbr\":50000,\"lvl1Nbr\":34,\"lvl2Nbr\":6420,\"lvl3Nbr\":12238,\"lvl4Nbr\":31526,\"finelineNbr\":5414,\"volumeDeviationStrategyLevelSelection\":3,\"styles\":[{\"styleId\":\"34_5414_1_22_2\",\"styleName\":null,\"channelType\":null,\"metrics\":null,\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"flowStrategy\":null,\"customerChoices\":[{\"ccId\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"ccName\":null,\"planId\":null,\"lvl0Nbr\":null,\"lvl1Nbr\":null,\"lvl2Nbr\":null,\"lvl3Nbr\":null,\"lvl4Nbr\":null,\"channelType\":null,\"finelineId\":null,\"styleId\":null,\"metrics\":null,\"fixtures\":[{\"fixtureType\":\"RACKS\",\"fixtureTypeRollupId\":3,\"metrics\":null,\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"clusters\":[{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":1,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":2,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":3,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":4,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":5,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null}],\"flowStrategy\":null,\"replenishments\":[{\"replnWeek\":12310,\"replnWeekDesc\":\"FYE2024WK10\",\"replnUnits\":625,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":2375,\"dcInboundAdjUnits\":null},{\"replnWeek\":12311,\"replnWeekDesc\":\"FYE2024WK11\",\"replnUnits\":1750,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12312,\"replnWeekDesc\":\"FYE2024WK12\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12314,\"replnWeekDesc\":\"FYE2024WK14\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null}],\"remainingUnits\":43207}],\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"flowStrategy\":null,\"replenishments\":[]},{\"ccId\":\"34_5414_1_22_2_MOODY SKIES\",\"ccName\":null,\"planId\":null,\"lvl0Nbr\":null,\"lvl1Nbr\":null,\"lvl2Nbr\":null,\"lvl3Nbr\":null,\"lvl4Nbr\":null,\"channelType\":null,\"finelineId\":null,\"styleId\":null,\"metrics\":null,\"fixtures\":[{\"fixtureType\":\"RACKS\",\"fixtureTypeRollupId\":3,\"metrics\":null,\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"clusters\":[{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":1,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":2,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":3,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":4,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":5,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null}],\"flowStrategy\":null,\"replenishments\":[{\"replnWeek\":12310,\"replnWeekDesc\":\"FYE2024WK10\",\"replnUnits\":625,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":2375,\"dcInboundAdjUnits\":null},{\"replnWeek\":12311,\"replnWeekDesc\":\"FYE2024WK11\",\"replnUnits\":1750,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12312,\"replnWeekDesc\":\"FYE2024WK12\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12314,\"replnWeekDesc\":\"FYE2024WK14\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null}],\"remainingUnits\":43207}],\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"flowStrategy\":null,\"replenishments\":[]},{\"ccId\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"ccName\":null,\"planId\":null,\"lvl0Nbr\":null,\"lvl1Nbr\":null,\"lvl2Nbr\":null,\"lvl3Nbr\":null,\"lvl4Nbr\":null,\"channelType\":null,\"finelineId\":null,\"styleId\":null,\"metrics\":null,\"fixtures\":[{\"fixtureType\":\"RACKS\",\"fixtureTypeRollupId\":3,\"metrics\":null,\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"clusters\":[{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":1,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":2,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":3,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":4,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":5,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null}],\"flowStrategy\":null,\"replenishments\":[{\"replnWeek\":12310,\"replnWeekDesc\":\"FYE2024WK10\",\"replnUnits\":625,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":2375,\"dcInboundAdjUnits\":null},{\"replnWeek\":12311,\"replnWeekDesc\":\"FYE2024WK11\",\"replnUnits\":1750,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12312,\"replnWeekDesc\":\"FYE2024WK12\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12314,\"replnWeekDesc\":\"FYE2024WK14\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null}],\"remainingUnits\":43207}],\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"flowStrategy\":null,\"replenishments\":[]},{\"ccId\":\"34_5414_1_22_2_RICH BLACK\",\"ccName\":null,\"planId\":null,\"lvl0Nbr\":null,\"lvl1Nbr\":null,\"lvl2Nbr\":null,\"lvl3Nbr\":null,\"lvl4Nbr\":null,\"channelType\":null,\"finelineId\":null,\"styleId\":null,\"metrics\":null,\"fixtures\":[{\"fixtureType\":\"RACKS\",\"fixtureTypeRollupId\":3,\"metrics\":null,\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"clusters\":[{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":1,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":2,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":3,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":4,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null},{\"volClusterDesc\":null,\"volClusterLevel\":null,\"analyticsClusterId\":5,\"strategyId\":null,\"flowStrategy\":3,\"metrics\":{\"inStoredate\":null,\"markdownDate\":null,\"sellingWeeks\":null,\"minPresentationUnits\":null,\"maxPresentationUnits\":null,\"totalStoresAllocated\":null,\"storeProductivity\":null},\"initialSet\":{\"weeksOfSale\":null,\"initialSetUnitsPerFix\":null,\"totalInitialSetUnits\":null},\"bumpList\":[],\"recon\":null}],\"flowStrategy\":null,\"replenishments\":[{\"replnWeek\":12310,\"replnWeekDesc\":\"FYE2024WK10\",\"replnUnits\":625,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":2375,\"dcInboundAdjUnits\":null},{\"replnWeek\":12311,\"replnWeekDesc\":\"FYE2024WK11\",\"replnUnits\":1750,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12312,\"replnWeekDesc\":\"FYE2024WK12\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null},{\"replnWeek\":12314,\"replnWeekDesc\":\"FYE2024WK14\",\"replnUnits\":null,\"adjReplnUnits\":null,\"remainingUnits\":43207,\"dcInboundUnits\":0,\"dcInboundAdjUnits\":null}],\"remainingUnits\":43207}],\"initialSet\":null,\"bumpList\":null,\"recon\":null,\"flowStrategy\":null,\"replenishments\":[]}],\"replenishment\":null}]}",BQFPResponse.class);
        APResponse rfaResponse = mapper.readValue("{\"rfaSizePackData\":[{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[5214, 32, 2712, 643, 3119, 2814, 5420, 818, 4332, 5087, 3505, 5299, 413, 1996]\",\"store_cnt\":14,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[5214, 32, 2712, 2814, 643, 3119, 5420, 818, 4332, 5087, 5299, 3505, 413, 1996]\",\"store_cnt\":14,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[5214, 32, 2712, 643, 3119, 2814, 5420, 818, 4332, 5087, 5299, 3505, 413, 1996]\",\"store_cnt\":14,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[2091]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[2091]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[5214, 32, 2712, 643, 2814, 3119, 5420, 818, 4332, 5087, 3505, 5299, 413, 1996]\",\"store_cnt\":14,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[2091]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":1,\"store_list\":\"[2091]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1068, 5301, 3858]\",\"store_cnt\":127,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1199, 2085, 2067, 2962, 2116, 5391]\",\"store_cnt\":6,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1199, 2085, 2067, 2962, 2116, 5391]\",\"store_cnt\":6,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1199, 2085, 2067, 2962, 2116, 5391]\",\"store_cnt\":6,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1068, 5301, 2405]\",\"store_cnt\":127,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1068, 5301, 2405]\",\"store_cnt\":127,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1068, 5301, 3858]\",\"store_cnt\":127,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":2,\"store_list\":\"[1199, 2067, 2085, 2962, 2116, 5391]\",\"store_cnt\":6,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[3478, 1939]\",\"store_cnt\":12,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[1474, 5003]\",\"store_cnt\":234,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[1854]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[1474, 5003]\",\"store_cnt\":234,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[3702, 5003, 814]\",\"store_cnt\":234,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[5326, 1195]\",\"store_cnt\":2,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[5326, 1195]\",\"store_cnt\":2,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[5326, 1195]\",\"store_cnt\":2,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[3478, 898, 1939, 1392, 5882, 2126, 3415, 2046, 1822, 5854, 1796, 8331]\",\"store_cnt\":12,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[1854]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[3702, 5003, 814, 3500]\",\"store_cnt\":234,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.25,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[1854]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[3478, 1939, 898, 1392, 5882, 2126, 3415, 2046, 1822, 5854, 1796, 8331]\",\"store_cnt\":12,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.25,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[1854]\",\"store_cnt\":1,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[3478, 1939, 898, 1392, 5882, 2126, 3415, 2046, 1822, 5854, 1796, 8331]\",\"store_cnt\":12,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":3,\"store_list\":\"[5326, 1195]\",\"store_cnt\":2,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[3660, 1809, 1322, 2644]\",\"store_cnt\":886,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[2440, 1453, 2005]\",\"store_cnt\":75,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[3660, 1809, 1322, 2644]\",\"store_cnt\":886,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[2440, 1453, 2005]\",\"store_cnt\":75,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[3660, 1809, 1322]\",\"store_cnt\":886,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[3660, 1809, 1322, 2644]\",\"store_cnt\":886,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[2476, 2585, 2441, 1411]\",\"store_cnt\":4,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[2440, 1453]\",\"store_cnt\":75,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[1453, 2440, 2005]\",\"store_cnt\":75,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[2476, 2585, 2441, 1411]\",\"store_cnt\":4,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[2476, 2441, 2585, 1411]\",\"store_cnt\":4,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":4,\"store_list\":\"[2476, 2441, 2585, 1411]\",\"store_cnt\":4,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2207, 356, 387, 2107, 3371]\",\"store_cnt\":2057,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2207, 356, 387]\",\"store_cnt\":2057,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2680, 5156, 1059, 2317]\",\"store_cnt\":4,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[3804, 843, 4241]\",\"store_cnt\":215,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[843, 3804, 4241]\",\"store_cnt\":215,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2680, 5156, 1059, 2317]\",\"store_cnt\":4,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2207, 356, 387]\",\"store_cnt\":2057,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2680, 5156, 1059, 2317]\",\"store_cnt\":4,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[3804, 843]\",\"store_cnt\":215,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_MOODY SKIES\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.125,\"color_family\":\"Blue\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2207, 356]\",\"store_cnt\":2057,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"fixture_type\":\"WALLS\",\"fixture_group\":0.125,\"color_family\":\"default\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[843, 3804]\",\"store_cnt\":215,\"plan_id_partition\":236},{\"rpt_lvl_0_nbr\":50000,\"rpt_lvl_1_nbr\":34,\"rpt_lvl_2_nbr\":6420,\"rpt_lvl_3_nbr\":12238,\"rpt_lvl_4_nbr\":31526,\"fineline_nbr\":5414,\"style_nbr\":\"34_5414_1_22_2\",\"customer_choice\":\"34_5414_1_22_2_RICH BLACK\",\"fixture_type\":\"RACKS\",\"fixture_group\":0.25,\"color_family\":\"Black\",\"size_cluster_id\":1,\"volume_group_cluster_id\":5,\"store_list\":\"[2680, 5156, 1059, 2317]\",\"store_cnt\":4,\"plan_id_partition\":236}]}", APResponse.class);
        BuyQtyResponse buyQtyResponse = mapper.readValue("{\"planId\":236,\"planDesc\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":null,\"lvl1Nbr\":34,\"lvl1Desc\":null,\"lvl2Nbr\":6420,\"lvl2Desc\":null,\"lvl3List\":[{\"lvl3Nbr\":12238,\"lvl3Desc\":null,\"metrics\":null,\"lvl4List\":[{\"lvl4Nbr\":31526,\"lvl4Desc\":null,\"metrics\":null,\"finelines\":[{\"finelineNbr\":5414,\"finelineDesc\":null,\"finelineAltDesc\":null,\"metrics\":{\"sizeProfilePct\":null,\"adjSizeProfilePct\":null,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"clusters\":null,\"styles\":[{\"styleNbr\":\"34_5414_1_22_2\",\"altStyleDesc\":null,\"metrics\":{\"sizeProfilePct\":null,\"adjSizeProfilePct\":null,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"clusters\":null,\"customerChoices\":[{\"ccId\":\"34_5414_1_22_2_FINISH LINE STRIPE\",\"colorName\":null,\"colorFamilyDesc\":null,\"altCcDesc\":null,\"metrics\":{\"sizeProfilePct\":null,\"adjSizeProfilePct\":null,\"avgSizeProfilePct\":97.72,\"adjAvgSizeProfilePct\":100.00999999999999,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"clusters\":[{\"clusterID\":0,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":18.92,\"adjAvgSizeProfilePct\":17.43,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":36.58,\"adjAvgSizeProfilePct\":33.69,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":30.94,\"adjAvgSizeProfilePct\":28.5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":11.28,\"adjAvgSizeProfilePct\":10.39,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]},{\"clusterID\":1,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":18.92,\"adjSizeProfilePct\":17.43,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":36.58,\"adjSizeProfilePct\":33.69,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":30.94,\"adjSizeProfilePct\":28.5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":11.28,\"adjSizeProfilePct\":10.39,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]}],\"merchMethods\":null,\"channelId\":null},{\"ccId\":\"34_5414_1_22_2_MOODY SKIES\",\"colorName\":null,\"colorFamilyDesc\":null,\"altCcDesc\":null,\"metrics\":{\"sizeProfilePct\":null,\"adjSizeProfilePct\":null,\"avgSizeProfilePct\":98.18,\"adjAvgSizeProfilePct\":100,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"clusters\":[{\"clusterID\":0,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":16.43,\"adjAvgSizeProfilePct\":15.06,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":37.53,\"adjAvgSizeProfilePct\":34.4,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":33.77,\"adjAvgSizeProfilePct\":30.96,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":10.45,\"adjAvgSizeProfilePct\":9.58,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]},{\"clusterID\":1,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":16.43,\"adjSizeProfilePct\":15.06,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":37.53,\"adjSizeProfilePct\":34.4,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":33.77,\"adjSizeProfilePct\":30.96,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":10.45,\"adjSizeProfilePct\":9.58,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]}],\"merchMethods\":null,\"channelId\":null},{\"ccId\":\"34_5414_1_22_2_PAINTERLY TROPICS\",\"colorName\":null,\"colorFamilyDesc\":null,\"altCcDesc\":null,\"metrics\":{\"sizeProfilePct\":null,\"adjSizeProfilePct\":null,\"avgSizeProfilePct\":97.72,\"adjAvgSizeProfilePct\":100.00999999999999,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"clusters\":[{\"clusterID\":0,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":18.92,\"adjAvgSizeProfilePct\":17.43,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":36.58,\"adjAvgSizeProfilePct\":33.69,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":30.94,\"adjAvgSizeProfilePct\":28.5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":11.28,\"adjAvgSizeProfilePct\":10.39,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]},{\"clusterID\":1,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":18.92,\"adjSizeProfilePct\":17.43,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":36.58,\"adjSizeProfilePct\":33.69,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":30.94,\"adjSizeProfilePct\":28.5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":11.28,\"adjSizeProfilePct\":10.39,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]}],\"merchMethods\":null,\"channelId\":null},{\"ccId\":\"34_5414_1_22_2_RICH BLACK\",\"colorName\":null,\"colorFamilyDesc\":null,\"altCcDesc\":null,\"metrics\":{\"sizeProfilePct\":null,\"adjSizeProfilePct\":null,\"avgSizeProfilePct\":97.23,\"adjAvgSizeProfilePct\":100,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"clusters\":[{\"clusterID\":0,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":21.53,\"adjAvgSizeProfilePct\":19.93,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":35.52,\"adjAvgSizeProfilePct\":32.88,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":29.19,\"adjAvgSizeProfilePct\":27.02,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":0,\"adjAvgSizeProfilePct\":5,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":0,\"avgSizeProfilePct\":10.99,\"adjAvgSizeProfilePct\":10.17,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]},{\"clusterID\":1,\"sizes\":[{\"ahsSizeId\":3176,\"sizeId\":null,\"sizeDesc\":\"L\",\"metrics\":{\"sizeProfilePct\":21.53,\"adjSizeProfilePct\":19.93,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3214,\"sizeId\":null,\"sizeDesc\":\"LT\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3233,\"sizeId\":null,\"sizeDesc\":\"M\",\"metrics\":{\"sizeProfilePct\":35.52,\"adjSizeProfilePct\":32.88,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3252,\"sizeId\":null,\"sizeDesc\":\"S\",\"metrics\":{\"sizeProfilePct\":29.19,\"adjSizeProfilePct\":27.02,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3271,\"sizeId\":null,\"sizeDesc\":\"S/M\",\"metrics\":{\"sizeProfilePct\":0,\"adjSizeProfilePct\":5,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null},{\"ahsSizeId\":3290,\"sizeId\":null,\"sizeDesc\":\"XL\",\"metrics\":{\"sizeProfilePct\":10.99,\"adjSizeProfilePct\":10.17,\"avgSizeProfilePct\":null,\"adjAvgSizeProfilePct\":null,\"buyQty\":null,\"bumpPackQty\":null,\"finalBuyQty\":null,\"finalInitialSetQty\":null,\"finalReplenishmentQty\":null,\"vendorPack\":null,\"warehousePack\":null,\"packRatio\":null,\"replenishmentPacks\":null},\"replenishments\":null}]}],\"merchMethods\":null,\"channelId\":null}],\"channelId\":null}],\"merchMethods\":[{\"fixtureType\":null,\"fixtureTypeRollupId\":1,\"merchMethod\":\"HANGING\",\"merchMethodCode\":1,\"metrics\":null,\"sizes\":null},{\"fixtureType\":null,\"fixtureTypeRollupId\":3,\"merchMethod\":\"HANGING\",\"merchMethodCode\":1,\"metrics\":null,\"sizes\":null}],\"channelId\":null}]}]}]}", BuyQtyResponse.class);
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2500);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 6420, 12238, 31526, 5414, 236L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(1)).findFirst().get();
        Set<SpCustomerChoiceChannelFixtureSize> fixture1Sizes = fixture1
                .getSpStyleChannelFixtures().stream().findFirst()
                .get().getSpCustomerChoiceChannelFixture().stream().findFirst()
                .get().getSpCustomerChoiceChannelFixtureSize();
        assertEquals(0, fixture1.getInitialSetQty());
        assertEquals(9504, fixture1.getBuyQty());
        assertEquals(9504, fixture1.getReplnQty());
    }


    private StrategyVolumeDeviationResponse strategyVolumeDeviationResponseFromJsonFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), StrategyVolumeDeviationResponse.class);
    }


    private SizeDto size48() {
        return createSize(4042, "48", 0.0, 0.024999);
    }

    private SizeDto createSize(Integer sizeId, String sizeDesc, Double sizeProfilePct, Double adjSizeProfilePct) {
        SizeDto size = new SizeDto();
        size.setSizeId(sizeId);
        size.setSizeDesc(sizeDesc);
        size.setMetrics(new MetricsDto());
        size.getMetrics().setAdjSizeProfilePct(adjSizeProfilePct);
        size.getMetrics().setSizeProfilePct(sizeProfilePct);
        return size;
    }


    void assertBumpSetStoreObject(Set<SpCustomerChoiceChannelFixtureSize> spCCFixSizes, long expectedTotalBumpPackUnits) {
        List<BumpSetQuantity> bumpSetQuantities = spCCFixSizes.stream()
                .map(SpCustomerChoiceChannelFixtureSize::getStoreObj)
                .map(this::deserialize).filter(Objects::nonNull)
                .map(BuyQtyStoreObj::getBuyQuantities)
                .flatMap(Collection::stream)
                .map(StoreQuantity::getBumpSets)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        long totalBumpPackStoreObjUnits = bumpSetQuantities.stream()
                .mapToDouble(BumpSetQuantity::getTotalUnits)
                .mapToLong(value -> (long) value)
                .sum();

        assertEquals(expectedTotalBumpPackUnits, totalBumpPackStoreObjUnits, "All bump qtys in store object should total up to expected total");
    }

    private BuyQtyStoreObj deserialize(String json) {
        try {
            return mapper.readValue(json, BuyQtyStoreObj.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Assertions.fail("Something happened deserializing store object");
        }
        return null;
    }

    private BuyQtyObj deserializeBuyQtyObj(String json) {
        try {
            return mapper.readValue(json, BuyQtyObj.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
           Assertions.fail("Something happened deserializing store object");
        }
        return null;
    }

    private void assertUnitValueBySize(Set<SpCustomerChoiceChannelFixtureSize> sizes, String sizeDesc, int expectedISQty, Function<SpCustomerChoiceChannelFixtureSize, Integer> unitsFunc) {
        sizes.stream().filter(spccFix -> spccFix.getAhsSizeDesc().equalsIgnoreCase(sizeDesc))
                .findFirst().ifPresentOrElse(spccFix -> assertFixtureSizeInitialSetValues(spccFix, expectedISQty, unitsFunc), () -> Assertions.fail(sizeDesc));
    }

    private void assertFixtureSizeInitialSetValues(SpCustomerChoiceChannelFixtureSize actual, int expectedISQty, Function<SpCustomerChoiceChannelFixtureSize, Integer> unitsFunc) {
        assertEquals(expectedISQty, (int) unitsFunc.apply(actual), String.format("Size %s should have correct value", actual.getAhsSizeDesc()));
    }

    BQFPResponse bqfpResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BQFPResponse.class);
    }

    APResponse apResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), APResponse.class);
    }

    BuyQtyResponse buyQtyResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BuyQtyResponse.class);
    }

    private String readTextFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".txt")));
    }

    String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }

    CalculateBuyQtyRequest create(String channel, int lvl0Nbr, int lvl1Nbr, int lvl2Nbr, int lvl3Nbr, int lvl4Nbr, int finelineNbr, Long planId) {
        CalculateBuyQtyRequest request = new CalculateBuyQtyRequest();
        request.setChannel(channel);
        request.setPlanId(planId);
        request.setLvl0Nbr(lvl0Nbr);
        request.setLvl1Nbr(lvl1Nbr);
        request.setLvl2Nbr(lvl2Nbr);
        request.setLvl3List(new ArrayList<>());

        Lvl3Dto lvl3 = new Lvl3Dto();
        lvl3.setLvl3Nbr(lvl3Nbr);
        lvl3.setLvl4List(new ArrayList<>());
        Lvl4Dto lvl4 = new Lvl4Dto();
        lvl4.setLvl4Nbr(lvl4Nbr);
        lvl4.setFinelines(new ArrayList<>());
        FinelineDto fineline = new FinelineDto();
        fineline.setFinelineNbr(finelineNbr);

        lvl3.getLvl4List().add(lvl4);
        lvl4.getFinelines().add(fineline);
        request.getLvl3List().add(lvl3);
        return request;
    }

    CalculateBuyQtyParallelRequest createFromRequest(CalculateBuyQtyRequest request) {
        CalculateBuyQtyParallelRequest pRequest = new CalculateBuyQtyParallelRequest();
        pRequest.setPlanId(request.getPlanId());
        pRequest.setChannel(request.getChannel());
        pRequest.setLvl0Nbr(request.getLvl0Nbr());
        pRequest.setLvl1Nbr(request.getLvl1Nbr());
        pRequest.setLvl2Nbr(request.getLvl2Nbr());
        pRequest.setLvl3Nbr(request.getLvl3List().get(0).getLvl3Nbr());
        pRequest.setLvl4Nbr(request.getLvl3List().get(0).getLvl4List().get(0).getLvl4Nbr());
        pRequest.setFinelineNbr(request.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        return pRequest;
    }

    @Test
    void multiMerchMethodTest() throws IOException, SizeAndPackException {
        final String path = "/plan72fineline250";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        CalculateBuyQtyRequest request = create("store", 50000, 23, 3669, 8244, 16906, 250, 12L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpCustomerChoiceChannelFixture fixture1 = response.getSpFineLineChannelFixtures()
                                                .stream()
                                                .map(SpFineLineChannelFixture::getSpStyleChannelFixtures)
                                                .flatMap(Collection::stream)
                                                .map(SpStyleChannelFixture::getSpCustomerChoiceChannelFixture)
                                                .flatMap(Collection::stream)
                                                .filter(customer -> customer.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("23_250_0_22_1_ANTHRACITE"))
                                                .findFirst().get();
        Set<SpCustomerChoiceChannelFixtureSize> fixture1Sizes = fixture1.getSpCustomerChoiceChannelFixtureSize();

        assertEquals(16, fixture1Sizes.size(), "Fixture 1 Should have 16 sizes present");
        int fix30x30 = 11791;
        int fix40x32 = 11448;
        int fix36x30 = 17443;
        int fix38x32 = 14045;
        int fix40x30 = 11399;
        int fix44x30 = 9617;
        int fix42x30 = 11742;
        int fix38x30 = 13891;
        int fix32x32 = 17356;
        int fix34x30 = 17356;
        int fix34x32 = 18017;
        int fix36x32 = 14757;
        int fix29x30 = 7026;
        int fix30x32 = 11425;
        int fix33x30 = 7210;
        int fix32x30 = 17077;
        int expectedTotalFix1InitialSetQty = IntStream.of(fix30x30, fix40x32, fix36x30, fix38x32, fix40x30, fix44x30, fix42x30,
                fix38x30, fix32x32, fix34x30, fix34x32, fix36x32, fix29x30, fix30x32, fix33x30, fix32x30).sum();
        assertUnitValueBySize(fixture1Sizes, "30X30", fix30x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "40X32", fix40x32, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "36X30", fix36x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "38X32", fix38x32, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "40X30", fix40x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "44X30", fix44x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "42X30", fix42x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "38X30", fix38x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "32X32", fix32x32, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "34X30", fix34x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "34X32", fix34x32, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "36X32", fix36x32, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "29X30", fix29x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "30X32", fix30x32, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "33X30", fix33x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "32X30", fix32x30, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertEquals(expectedTotalFix1InitialSetQty, (int) fixture1.getInitialSetQty(), "Fixture 1 Initial Set Qty rollup should be sum of all size values");
    }

    @Test
    void test_calculateFinelineBuyQtyShouldUpdateBumpPackCount() throws SizeAndPackException, IOException {
        final String path = "/plan72fineline1500";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponseWithBumpList"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9071, 7205, 1500, 12L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        assertEquals(1, response.getSpFineLineChannelFixtures().size());
        assertEquals(4, response.getSpFineLineChannelFixtures().iterator().next().getBumpPackCnt());
    }

    @Test
    void test_getCcMaxBumpPackCnt(){
        StyleDto style = new StyleDto();
        style.setStyleNbr("34_1234_001");
        CustomerChoiceDto customerChoice = new CustomerChoiceDto();
        customerChoice.setCcId("1234_001");
        List<Style> styles = new ArrayList<>();
        Style st = new Style();
        st.setStyleId("34_1234_001");
        BQFPResponse bqfpResponse = new BQFPResponse(12L,50000,34,6419,12228,31507,2702,styles);

        List<CustomerChoice> ccs = new ArrayList<>();
        CustomerChoice cc1 = new CustomerChoice();
        cc1.setCcId("1234_001");
        cc1.setFixtures(BQFPResponseInputs.getFixtureList(2,"RACKS","WALLS"));

        CustomerChoice cc2 = new CustomerChoice();
        cc2.setCcId("1234_002");
        cc2.setFixtures(BQFPResponseInputs.getFixtureList(1,"RACKS"));

        ccs.add(cc1);
        ccs.add(cc2);
        st.setCustomerChoices(ccs);
        styles.add(st);
        bqfpResponse.setStyles(styles);
        Integer result = calculateFinelineBuyQuantity.getCcMaxBumpPackCnt(bqfpResponse,style,customerChoice);
        assertEquals(3, result);

    }

}
