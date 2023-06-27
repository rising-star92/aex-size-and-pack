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
import com.walmart.aex.sp.properties.BuyQtyProperties;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.service.impl.DeptAdminRuleServiceImpl;
import com.walmart.aex.sp.util.BQFPResponseInputs;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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


   @Spy
   ObjectMapper mapper = new ObjectMapper();

    @BeforeEach

    void setUp() {
       MockitoAnnotations.openMocks(this);
       calculateInitialSetQuantityService = new CalculateInitialSetQuantityService();
       calculateBumpPackQtyService = new CalculateBumpPackQtyService();
       buyQuantityConstraintService = new BuyQuantityConstraintService(calculateBumpPackQtyService);
       addStoreBuyQuantityService = new AddStoreBuyQuantityService(mapper, calculateBumpPackQtyService, buyQuantityConstraintService, calculateInitialSetQuantityService, buyQtyProperties);
       replenishmentsOptimizationServices=new ReplenishmentsOptimizationService(deptAdminRuleService);
       BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService = new BuyQtyReplenishmentMapperService();
       replenishmentService = new ReplenishmentService(fineLineReplenishmentRepository, spCustomerChoiceReplenishmentRepository, sizeListReplenishmentRepository, catgReplnPkConsRepository,
               subCatgReplnPkConsRepository, finelineReplnPkConsRepository, styleReplnConsRepository, ccReplnConsRepository,
               ccMmReplnPkConsRepository, ccSpReplnPkConsRepository, replenishmentMapper, updateReplnConfigMapper, buyQuantityMapper,
               strategyFetchService, buyQtyCommonUtil, sizeLevelReplenishmentRepository,sizeLevelReplenishmentMapper);
       calculateOnlineFinelineBuyQuantity = new  CalculateOnlineFinelineBuyQuantity (mapper, buyQtyReplenishmentMapperService,replenishmentsOptimizationServices, replenishmentService );
       calculateFinelineBuyQuantity = new CalculateFinelineBuyQuantity(bqfpService, mapper, buyQtyReplenishmentMapperService, calculateOnlineFinelineBuyQuantity,
               strategyFetchService,addStoreBuyQuantityService, buyQuantityConstraintService, deptAdminRuleService, replenishmentService, replenishmentsOptimizationServices);
       lenient().when(buyQtyProperties.getOneUnitPerStoreFeatureFlag()).thenReturn("true");
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
        assertEquals((Integer)18474,
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
        assertEquals(1308, fixture1.getInitialSetQty());
        assertEquals(9612, fixture1.getBuyQty());
        assertEquals(8304, fixture1.getReplnQty());
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
        int fix44x30 = 9708;
        int fix42x30 = 11807;
        int fix38x30 = 13891;
        int fix32x32 = 17356;
        int fix34x30 = 17356;
        int fix34x32 = 18017;
        int fix36x32 = 14757;
        int fix29x30 = 7215;
        int fix30x32 = 11425;
        int fix33x30 = 7513;
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
        int fix1xs = 3716;
        int fix1s = 13028;
        int fix1m = 16070;
        int fix1l = 16989;
        int fix1xl = 13187;
        int fix1xxl = 9932;
        int fix1xxxl = 3926;
        assertUnitValueBySize(fixture1Sizes, "XS", fix1xs, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "S", fix1s, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "M", fix1m, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "L", fix1l, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XL", fix1xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XXL", fix1xxl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
        assertUnitValueBySize(fixture1Sizes, "XXXL", fix1xxxl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
    }

}
