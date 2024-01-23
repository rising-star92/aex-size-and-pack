package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.CustomerChoice;
import com.walmart.aex.sp.dto.bqfp.Style;
import com.walmart.aex.sp.dto.buyquantity.BumpSetQuantity;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyObj;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyStoreObj;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StoreQuantity;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpStyleChannelFixture;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.SizeLevelReplenishmentRepository;
import com.walmart.aex.sp.repository.SizeListReplenishmentRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceReplenishmentRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.repository.StyleReplenishmentRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import com.walmart.aex.sp.service.impl.DeptAdminRuleServiceImpl;
import com.walmart.aex.sp.util.BQFPResponseInputs;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
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

    @Mock
    ReplenishmentService replenishmentService;

    @Mock
    ReplenishmentsOptimizationService replenishmentsOptimizationServices;

    @Mock
    CatgReplnPkConsRepository catgReplnPkConsRepository;

    @Mock
    SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

    @Mock
    FinelineReplnPkConsRepository finelineReplnPkConsRepository;

    @Mock
    StyleReplnPkConsRepository styleReplnConsRepository;

    @Mock
    CcReplnPkConsRepository ccReplnConsRepository;

    @Mock
    CcMmReplnPkConsRepository ccMmReplnPkConsRepository;

    @Mock
    CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    @Mock
    SpCustomerChoiceReplenishmentRepository  spCustomerChoiceReplenishmentRepository;

    @Mock
    SizeListReplenishmentRepository sizeListReplenishmentRepository;

    @Mock
    ReplenishmentMapper replenishmentMapper;

    @Mock
    UpdateReplnConfigMapper updateReplnConfigMapper;

    @Mock
    BuyQuantityMapper buyQuantityMapper;

    @Mock
    BuyQtyCommonUtil buyQtyCommonUtil;

    @Mock
    SizeLevelReplenishmentRepository sizeLevelReplenishmentRepository;

    @Mock
    SizeLevelReplenishmentMapper sizeLevelReplenishmentMapper;

    @Mock
    BuyQtyProperties buyQtyProperties;

    @Mock
    MidasServiceCall midasServiceCall;

    @Mock
    LinePlanService linePlanService;

    @Mock
    BigQueryClusterService bigQueryClusterService;

    @Mock
    ValidationService validationService;

   @Spy
   ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws SizeAndPackException {
       MockitoAnnotations.openMocks(this);
       calculateInitialSetQuantityService = new CalculateInitialSetQuantityService();
       calculateBumpPackQtyService = new CalculateBumpPackQtyService();
       buyQuantityConstraintService = new BuyQuantityConstraintService(calculateBumpPackQtyService);
       addStoreBuyQuantityService = new AddStoreBuyQuantityService(mapper, calculateBumpPackQtyService, buyQuantityConstraintService, calculateInitialSetQuantityService, buyQtyProperties);
       replenishmentsOptimizationServices = new ReplenishmentsOptimizationService(deptAdminRuleService);
       BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService = new BuyQtyReplenishmentMapperService();
       replenishmentService = new ReplenishmentService(fineLineReplenishmentRepository, spCustomerChoiceReplenishmentRepository, sizeListReplenishmentRepository, catgReplnPkConsRepository,
               subCatgReplnPkConsRepository, finelineReplnPkConsRepository, styleReplnConsRepository, ccReplnConsRepository,
               ccMmReplnPkConsRepository, ccSpReplnPkConsRepository, replenishmentMapper, updateReplnConfigMapper, buyQuantityMapper,
               strategyFetchService, buyQtyCommonUtil, sizeLevelReplenishmentRepository,sizeLevelReplenishmentMapper);
       calculateOnlineFinelineBuyQuantity = new  CalculateOnlineFinelineBuyQuantity (mapper, buyQtyReplenishmentMapperService,replenishmentsOptimizationServices, replenishmentService );
       calculateFinelineBuyQuantity = new CalculateFinelineBuyQuantity(bqfpService, mapper, buyQtyReplenishmentMapperService, calculateOnlineFinelineBuyQuantity,
               strategyFetchService,addStoreBuyQuantityService, buyQuantityConstraintService, deptAdminRuleService, replenishmentService, replenishmentsOptimizationServices,
               midasServiceCall, linePlanService, bigQueryClusterService, calculateInitialSetQuantityService, calculateBumpPackQtyService, validationService);
       setProperties();
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
        int fix1xs = 6413;
        int fix1s = 18055;
        int fix1m = 31460;
        int fix1l = 32954;
        int fix1xl = 21399;
        int fix1xxl = 12441;
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
              response.getMerchCatgReplPacks().get(0).getReplUnits(), "Repln units should be 18417 for cc");

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
        final String path = "/plan236fineline5414";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 6420, 12238, 31526, 5414, 236L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(1)).findFirst().get();
        SpCustomerChoiceChannelFixture ccFixture = fixture1
                .getSpStyleChannelFixtures().stream().findFirst()
                .get().getSpCustomerChoiceChannelFixture().stream().filter(cc -> cc.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("34_5414_1_22_2_RICH BLACK"))
                .findFirst().get();
        assertEquals(0, ccFixture.getInitialSetQty());
        assertEquals(0, fixture1.getInitialSetQty());
        assertEquals(9612, fixture1.getBuyQty());
        assertEquals(9612, fixture1.getReplnQty());
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

        List<SpFineLineChannelFixture> spFineLineChannelFixtures = response.getSpFineLineChannelFixtures();
        assertEquals("[203,204]", spFineLineChannelFixtures.get(0).getAppMessageObj());
        assertEquals("[203,204]", spFineLineChannelFixtures.get(0).getSpStyleChannelFixtures().iterator().next().getAppMessageObj());
        Set<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixture = spFineLineChannelFixtures.get(0).getSpStyleChannelFixtures().iterator().next().getSpCustomerChoiceChannelFixture();
        List<String> appMsgCC = spCustomerChoiceChannelFixture.stream().map(SpCustomerChoiceChannelFixture::getAppMessageObj).collect(Collectors.toList());
        assertEquals(4, appMsgCC.size());
        assertEquals(3, appMsgCC.stream().filter(Objects::nonNull).filter(val -> val.equals("[203,204]")).count());
        List<Set<SpCustomerChoiceChannelFixtureSize>> spCustomerChoiceChannelFixtureSizeList = spCustomerChoiceChannelFixture.stream().map(SpCustomerChoiceChannelFixture::getSpCustomerChoiceChannelFixtureSize).collect(Collectors.toList());
        assertEquals(4, spCustomerChoiceChannelFixtureSizeList.size());
        assertEquals(64, (int) spCustomerChoiceChannelFixtureSizeList.stream().flatMap(Collection::stream).count());
        assertEquals(4, (int) spCustomerChoiceChannelFixtureSizeList.stream().flatMap(Collection::stream).filter(val -> val.getAhsSizeDesc().equals("33X30")).count());
        assertEquals(3, (int) spCustomerChoiceChannelFixtureSizeList.stream().flatMap(Collection::stream).filter(val -> val.getAhsSizeDesc().equals("33X30")).map(SpCustomerChoiceChannelFixtureSize::getAppMessageObj).filter(Objects::nonNull).filter(val -> val.equals("[203,204]")).count());


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
        int fix30x30 = 11544;
        int fix40x32 = 11448;
        int fix36x30 = 17403;
        int fix38x32 = 14076;
        int fix40x30 = 11445;
        int fix44x30 = 9673;
        int fix42x30 = 11840;
        int fix38x30 = 13983;
        int fix32x32 = 17240;
        int fix34x30 = 17257;
        int fix34x32 = 19745;
        int fix36x32 = 16303;
        int fix29x30 = 7215;
        int fix30x32 = 11412;
        int fix33x30 = 7571;
        int fix32x30 = 17133;
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

    @Test
    void test_calculateInitialSetWithAtleast1UnitPerStore() throws SizeAndPackException, IOException {
        final String path = "/plan69fineline468";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/VDResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        when(deptAdminRuleService.getReplenishmentThreshold(anyLong(), anyInt())).thenReturn(500);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9074, 7207, 468, 69L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(1)).findFirst().get();
        Set<SpCustomerChoiceChannelFixtureSize> fixture1Sizes = fixture1
                .getSpStyleChannelFixtures().stream().filter(style -> style.getSpStyleChannelFixtureId().getStyleNbr().equals("34_468_3_24_004"))
                .findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().filter(cc -> cc.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("34_468_3_24_004_003"))
                .findFirst().get().getSpCustomerChoiceChannelFixtureSize();

        assertEquals(7, fixture1Sizes.size(), "Fixture 1 Should have 7 sizes present");
        int fix1xs = 4067;
        int fix1s = 13028;
        int fix1m = 16070;
        int fix1l = 16989;
        int fix1xl = 13187;
        int fix1xxl = 9680;
        int fix1xxxl = 3926;
        assertUnitValueBySize(fixture1Sizes, "XS", fix1xs, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "S", fix1s, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "M", fix1m, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "L", fix1l, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XL", fix1xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XXL", fix1xxl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XXXL", fix1xxxl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
    }

    @Test
    void test_calculateInitialSetWithOneUnitAllForReduction() throws SizeAndPackException, IOException {
        final String path = "/plan62fineline5149";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/VDResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        when(deptAdminRuleService.getReplenishmentThreshold(anyLong(), anyInt())).thenReturn(500);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 6419, 12231, 31513, 5149, 62L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(1)).findFirst().get();
        Set<SpCustomerChoiceChannelFixtureSize> fixtureSizes = fixture1
                .getSpStyleChannelFixtures().stream().filter(style -> style.getSpStyleChannelFixtureId().getStyleNbr().equals("34_5149_2_22_4"))
                .findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().filter(cc -> cc.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("34_5149_2_22_4_001"))
                .findFirst().get().getSpCustomerChoiceChannelFixtureSize();

        assertEquals(6, fixtureSizes.size(), "Fixture 1 Should have 6 sizes present");
        int fix0X = 3587;
        int fix1X = 6739;
        int fix2X = 7174;
        int fix3X = 8081;
        int fix4X = 5568;
        int fix5X = 3587;
        assertUnitValueBySize(fixtureSizes, "0X", fix0X, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixtureSizes, "1X", fix1X, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixtureSizes, "2X", fix2X, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixtureSizes, "3X", fix3X, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixtureSizes, "4X", fix4X, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixtureSizes, "5X", fix5X, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
    }

    @Test
    void test_calculateInitialSetWithCombinationOfZeroAndGreaterThanZeroInitialSet() throws SizeAndPackException, IOException {
        final String path = "/plan133fineline3347";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/VDResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        when(deptAdminRuleService.getReplenishmentThreshold(anyLong(), anyInt())).thenReturn(750);
        CalculateBuyQtyRequest request = create("store", 50000, 23, 3669, 8244, 16906, 3347, 133L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(1)).findFirst().get();
        SpCustomerChoiceChannelFixture customerChoiceChannelFixture1 = fixture1
                .getSpStyleChannelFixtures().stream().filter(style -> style.getSpStyleChannelFixtureId().getStyleNbr().equals("23_3347_1_25_001"))
                .findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().filter(cc -> cc.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("23_3347_1_25_001_001"))
                .findFirst().get();
        SpCustomerChoiceChannelFixture customerChoiceChannelFixture2 = fixture1
                .getSpStyleChannelFixtures().stream().filter(style -> style.getSpStyleChannelFixtureId().getStyleNbr().equals("23_3347_1_25_001"))
                .findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().filter(cc -> cc.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("23_3347_1_25_001_002"))
                .findFirst().get();

        assertEquals(75298, customerChoiceChannelFixture1.getInitialSetQty(), "CC which got initial set from BQFP will go through initialSetCalculation");
        assertEquals(45724, customerChoiceChannelFixture1.getReplnQty(), "Replenishments are calculated with initial sets");

        assertEquals(0, customerChoiceChannelFixture2.getInitialSetQty(), "CC which doesn't got any initialSet from BQFP will not have initial sets");
        assertEquals(101736, customerChoiceChannelFixture2.getReplnQty(), "Replenishments are calculated when no initial sets are available");

        assertEquals(75298, fixture1.getInitialSetQty(), "At fineline level, the initialSet value is coming from only one CC");

    }

    @Test
    void test_calculateInitialSetWithCombinationOfZeroAndGreaterThanZeroInitialSet1() throws SizeAndPackException, IOException {
        final String path = "/plan73fineline2810";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/VDResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        when(deptAdminRuleService.getReplenishmentThreshold(anyLong(), anyInt())).thenReturn(2500);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 6419, 12228, 31507, 2810, 73L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(2)).findFirst().get();
        Set<SpCustomerChoiceChannelFixtureSize> customerChoiceChannelFixtureSize = fixture1
                .getSpStyleChannelFixtures().stream().filter(style -> style.getSpStyleChannelFixtureId().getStyleNbr().equals("34_2810_4_21_6"))
                .findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().filter(cc -> cc.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("34_2810_4_21_6_CRLBSM"))
                .findFirst().get().getSpCustomerChoiceChannelFixtureSize();

        customerChoiceChannelFixtureSize.forEach(size -> {
            try {
                assertEquals(size.getInitialSetQty(), getIsQty(size.getStoreObj()), "Total IS Qty of size " + size.getAhsSizeDesc() + " should be equal to the size of size/volume cluster IS Units");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private int getIsQty(String storeObj) throws JsonProcessingException {
        BuyQtyStoreObj buyQtyStoreObj = mapper.readValue(storeObj, BuyQtyStoreObj.class);
        return (int) Math.round(buyQtyStoreObj.getBuyQuantities()
                .stream()
                .filter(Objects::nonNull)
                .mapToDouble(storeQuantity -> Optional.ofNullable(storeQuantity.getTotalUnits()).orElse((double) 0))
                .sum());
    }

    @Test
    void test_calculateBuyQtyWithGCPPayload() throws SizeAndPackException, IOException, InterruptedException {
        final String path = "/plan123fineline771";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseFromJsonFromJson(path.concat("/VDResponse"));
        when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        when(bigQueryClusterService.fetchRFASizePackData(any(), any())).thenReturn(rfaResponse.getRfaSizePackData());
        when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
        when(deptAdminRuleService.getInitialThreshold(anyLong(), anyInt())).thenReturn(2);
        when(deptAdminRuleService.getReplenishmentThreshold(anyLong(), anyInt())).thenReturn(500);
        CalculateBuyQtyRequest request = create("store", 50000, 23, 3669, 7506, 16887, 771, 123L);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQtyV2(request, pRequest, r);

        SpFineLineChannelFixture fixture1 = response.getSpFineLineChannelFixtures().stream().
                filter(f -> f.getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(1)).findFirst().get();
        Set<SpCustomerChoiceChannelFixtureSize> fixture1Sizes = fixture1
                .getSpStyleChannelFixtures().stream().filter(style -> style.getSpStyleChannelFixtureId().getStyleNbr().equals("23_771_1_25_002"))
                .findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().filter(cc -> cc.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals("23_771_1_25_002_002"))
                .findFirst().get().getSpCustomerChoiceChannelFixtureSize();

        assertEquals(6, fixture1Sizes.size(), "Fixture 1 Should have 6 sizes present");
        int fixs = 2368;
        int fixm = 2572;
        int fixl = 3858;
        int fix1xl = 2572;
        int fix2xl = 2610;
        int fix3xl = 2328;
        assertUnitValueBySize(fixture1Sizes, "S", fixs, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "M", fixm, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "L", fixl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XL", fix1xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "2XL", fix2xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "3XL", fix3xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
    }

    private void setProperties() throws SizeAndPackException {
        ReflectionTestUtils.setField(calculateFinelineBuyQuantity, "buyQtyProperties", buyQtyProperties);
        lenient().when(buyQtyProperties.getOneUnitPerStoreFeatureFlag()).thenReturn("true");
        lenient().when(buyQtyProperties.getDeviationFlag()).thenReturn("true");
        lenient().when(midasServiceCall.fetchColorFamilies(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of("DEFAULT"));
        lenient().when(linePlanService.getLikeAssociation(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
    }

}
