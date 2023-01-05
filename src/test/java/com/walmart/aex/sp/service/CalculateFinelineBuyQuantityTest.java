package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpStyleChannelFixture;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.repository.StyleReplenishmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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

import java.io.IOException;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

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
    BuyQtyProperties buyQtyProperties;

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

    ReplenishmentsOptimizationService replenishmentsOptimizationServices;

   @Spy
   ObjectMapper mapper = new ObjectMapper();

    @BeforeEach

    void setUp() {
       MockitoAnnotations.openMocks(this);
       calculateInitialSetQuantityService = new CalculateInitialSetQuantityService();
       calculateBumpPackQtyService = new CalculateBumpPackQtyService();
       buyQuantityConstraintService = new BuyQuantityConstraintService(calculateBumpPackQtyService, buyQtyProperties);
       addStoreBuyQuantityService = new AddStoreBuyQuantityService(mapper, calculateBumpPackQtyService, buyQuantityConstraintService, calculateInitialSetQuantityService, buyQtyProperties);
       replenishmentsOptimizationServices=new ReplenishmentsOptimizationService();

       calculateOnlineFinelineBuyQuantity = new  CalculateOnlineFinelineBuyQuantity (mapper, new BuyQtyReplenishmentMapperService(),replenishmentsOptimizationServices );
       calculateFinelineBuyQuantity = new CalculateFinelineBuyQuantity(bqfpService, mapper, new BuyQtyReplenishmentMapperService(), calculateOnlineFinelineBuyQuantity,
               strategyFetchService,spFineLineChannelFixtureRepository,styleReplenishmentRepository, fineLineReplenishmentRepository, addStoreBuyQuantityService, buyQuantityConstraintService);
    }

    @Test
    void initialSetCalculationTest() throws SizeAndPackException, IOException {
       final String path = "/plan72fineline1500";
       BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
       APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
       BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
       Mockito.when(buyQtyProperties.getInitialThreshold()).thenReturn(2);
       Mockito.when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
       Mockito.when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
       Mockito.when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
       Mockito.when(buyQtyProperties.getInitialThreshold()).thenReturn(2);
       CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9071, 7205, 1500);
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

        assertEquals("Fixture 1 Should have 6 sizes present", 6, fixture1Sizes.size());
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
        assertEquals("Fixture 1 Initial Set Qty rollup should be sum of all size values", expectedTotalFix1InitialSetQty, (int) fixture1.getInitialSetQty());
    }

    @Test
    void replenishmentCalculationTest() throws IOException, SizeAndPackException {
        final String path = "/plan72fineline4440";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        Mockito.when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        Mockito.when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        Mockito.when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9074, 7207, 4440);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);

        Set<SpCustomerChoiceChannelFixtureSize> spCcChanFixSizes = new HashSet<>(response.getSpFineLineChannelFixtures().get(1)
                .getSpStyleChannelFixtures().stream().findFirst().get()
                .getSpCustomerChoiceChannelFixture().stream().findFirst().get()
                .getSpCustomerChoiceChannelFixtureSize());

        long actualReplUnitsBySize = spCcChanFixSizes.stream().mapToLong(SpCustomerChoiceChannelFixtureSize::getReplnQty).sum();

        long expectedTotalReplnUnits = 19133;
        assertNotNull(response.getMerchCatgReplPacks());
        assertEquals("Only 1 merch catg repl pack created", 1, response.getMerchCatgReplPacks().size());
    }

    @Test
    void bumpSetCalculationTest() throws IOException, SizeAndPackException {
        final String path = "/plan72fineline4440";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponseWithBumpSet"));
        APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
        Mockito.when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        Mockito.when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        Mockito.when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        CalculateBuyQtyRequest request = create("store", 50000, 34, 1488, 9074, 7207, 4440);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

        CalculateBuyQtyResponse response = calculateFinelineBuyQuantity.calculateFinelineBuyQty(request, pRequest, r);
        SpFineLineChannelFixture spflChFix = response.getSpFineLineChannelFixtures().get(1);
        int expectedTotalBumpPackQty = 17361;
        assertEquals(expectedTotalBumpPackQty, (long) spflChFix.getBumpPackQty());

        SpStyleChannelFixture spStlChFix = spflChFix.getSpStyleChannelFixtures().stream().findFirst().get();
        assertEquals(expectedTotalBumpPackQty, (long) spStlChFix.getBumpPackQty());

        SpCustomerChoiceChannelFixture spCCChFix = spStlChFix.getSpCustomerChoiceChannelFixture().stream().findFirst().get();
        assertEquals(expectedTotalBumpPackQty, (long) spCCChFix.getBumpPackQty());

        Set<SpCustomerChoiceChannelFixtureSize> spCCFixSizes = spCCChFix.getSpCustomerChoiceChannelFixtureSize();

        assertEquals("Should have 7 sizes", 7, spCCFixSizes.size());
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
        assertEquals("Total of all size bump pack qtys should match", expectedTotalBumpPackQtySize, actualTotalBumpPackQty);
        assertBumpSetStoreObject(spCCFixSizes, expectedTotalBumpPackQtySize);
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

        assertEquals("All bump qtys in store object should total up to expected total",
                expectedTotalBumpPackUnits, totalBumpPackStoreObjUnits);
    }

    private BuyQtyStoreObj deserialize(String json) {
        try {
            return mapper.readValue(json, BuyQtyStoreObj.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Assert.fail("Something happened deserializing store object");
        }
        return null;
    }

    private BuyQtyObj deserializeBuyQtyObj(String json) {
        try {
            return mapper.readValue(json, BuyQtyObj.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Assert.fail("Something happened deserializing store object");
        }
        return null;
    }

    private void assertUnitValueBySize(Set<SpCustomerChoiceChannelFixtureSize> sizes, String sizeDesc, int expectedISQty, Function<SpCustomerChoiceChannelFixtureSize, Integer> unitsFunc) {
        sizes.stream().filter(spccFix -> spccFix.getAhsSizeDesc().equalsIgnoreCase(sizeDesc))
                .findFirst().ifPresentOrElse(spccFix -> assertFixtureSizeInitialSetValues(spccFix, expectedISQty, unitsFunc), () -> Assert.fail(sizeDesc));
    }

    private void assertFixtureSizeInitialSetValues(SpCustomerChoiceChannelFixtureSize actual, int expectedISQty, Function<SpCustomerChoiceChannelFixtureSize, Integer> unitsFunc) {
        assertEquals(String.format("Size %s should have correct value", actual.getAhsSizeDesc()), expectedISQty, (int) unitsFunc.apply(actual));
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

    CalculateBuyQtyRequest create(String channel, int lvl0Nbr, int lvl1Nbr, int lvl2Nbr, int lvl3Nbr, int lvl4Nbr, int finelineNbr) {
        CalculateBuyQtyRequest request = new CalculateBuyQtyRequest();
        request.setChannel(channel);
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
        Mockito.when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
        Mockito.when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
        Mockito.when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
        CalculateBuyQtyRequest request = create("store", 50000, 23, 3669, 8244, 16906, 250);
        CalculateBuyQtyParallelRequest pRequest = createFromRequest(request);

        CalculateBuyQtyResponse r = new CalculateBuyQtyResponse();
        r.setMerchCatgReplPacks(new ArrayList<>());
        r.setSpFineLineChannelFixtures(new ArrayList<>());

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

        assertEquals("Fixture 1 Should have 16 sizes present", 16, fixture1Sizes.size());
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
        assertEquals("Fixture 1 Initial Set Qty rollup should be sum of all size values", expectedTotalFix1InitialSetQty, (int) fixture1.getInitialSetQty());
    }
}
