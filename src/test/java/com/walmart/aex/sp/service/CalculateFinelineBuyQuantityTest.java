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
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.util.AdjustedDCInboundQty;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Slf4j
public class CalculateFinelineBuyQuantityTest {

    @InjectMocks
    public CalculateFinelineBuyQuantity calculateFinelineBuyQuantity;

    @Mock
    private SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    @Mock
    BQFPService bqfpService;

    @Spy
    AddStoreBuyQuantitiesService addStoreBuyQuantitiesService;

    @Mock
    CalculateInitialSetQuantityService  calculateInitialSetQuantityService;

    @Mock
    CalculateBumpPackQtyService calculateBumpPackQtyService;

    @Mock
    BuyQtyProperties buyQtyProperties;

    @Mock
    BuyQuantityConstraintService buyQuantityConstraintService;

    @Mock
    private MerchCatgReplPackRepository merchCatgReplPackRepository;

   @InjectMocks
    public CalculateOnlineFinelineBuyQuantity calculateOnlineFinelineBuyQuantity;

   @Mock
   StrategyFetchService strategyFetchService;

   ReplenishmentsOptimizationService replenishmentsOptimizationServices;

   AdjustedDCInboundQty adjustedDCInboundQty;

   @Spy
   ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
       MockitoAnnotations.openMocks(this);
       calculateInitialSetQuantityService = new CalculateInitialSetQuantityService();
       calculateBumpPackQtyService = new CalculateBumpPackQtyService();
       buyQuantityConstraintService = new BuyQuantityConstraintService(calculateBumpPackQtyService, buyQtyProperties);
       addStoreBuyQuantitiesService = new AddStoreBuyQuantitiesService(mapper, calculateBumpPackQtyService, buyQuantityConstraintService, calculateInitialSetQuantityService, buyQtyProperties);
       adjustedDCInboundQty = new AdjustedDCInboundQty();
       replenishmentsOptimizationServices=new ReplenishmentsOptimizationService(adjustedDCInboundQty);

       calculateOnlineFinelineBuyQuantity = new  CalculateOnlineFinelineBuyQuantity (mapper, new BuyQtyReplenishmentMapperService(),replenishmentsOptimizationServices );
       calculateFinelineBuyQuantity = new CalculateFinelineBuyQuantity(bqfpService, mapper, new BuyQtyReplenishmentMapperService(), calculateOnlineFinelineBuyQuantity,
               strategyFetchService,spFineLineChannelFixtureRepository,merchCatgReplPackRepository, addStoreBuyQuantitiesService, buyQuantityConstraintService);
    }

    @Test
    public void initialSetCalculationTest() throws SizeAndPackException, IOException {
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
       SpFineLineChannelFixture fixture3 = response.getSpFineLineChannelFixtures().get(1);
       Set<SpCustomerChoiceChannelFixtureSize> fixture1Sizes = fixture1
             .getSpStyleChannelFixtures().stream().findFirst()
             .get().getSpCustomerChoiceChannelFixture().stream().findFirst()
             .get().getSpCustomerChoiceChannelFixtureSize();
       Set<SpCustomerChoiceChannelFixtureSize> fixture3Sizes = fixture3
             .getSpStyleChannelFixtures().stream().findFirst()
             .get().getSpCustomerChoiceChannelFixture().stream().findFirst()
             .get().getSpCustomerChoiceChannelFixtureSize();

       assertEquals("Fixture 1 Should have 6 sizes present", 6, fixture1Sizes.size());
       int fix1xs = 4201;
       int fix1s = 11321;
       int fix1m = 19740;
       int fix1l = 20627;
       int fix1xl = 13663;
       int fix1xxl = 7397;
       int expectedTotalFix1InitialSetQty = IntStream.of(fix1xs, fix1s, fix1m, fix1l, fix1xl, fix1xxl).sum();
       assertUnitValueBySize(fixture1Sizes, "XS", fix1xs, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture1Sizes, "S", fix1s, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture1Sizes, "M", fix1m, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture1Sizes, "L", fix1l, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture1Sizes, "XL", fix1xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture1Sizes, "XXL", fix1xxl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertEquals("Fixture 1 Initial Set Qty rollup should be sum of all size values", expectedTotalFix1InitialSetQty, (int)fixture1.getInitialSetQty());

       assertEquals("Fixture 1 Should have 6 sizes present", 6, fixture1Sizes.size());
       int fix3xs = 2392;
       int fix3s = 6235;
       int fix3m = 11050;
       int fix3l = 11977;
       int fix3xl = 7420;
       int fix3xxl = 4499;
       int expectedTotalFix3InitialSetQty = IntStream.of(fix3xs, fix3s, fix3m, fix3l, fix3xl, fix3xxl).sum();
       assertUnitValueBySize(fixture3Sizes, "XS", fix3xs, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture3Sizes, "S", fix3s, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture3Sizes, "M", fix3m, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture3Sizes, "L", fix3l, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture3Sizes, "XL", fix3xl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertUnitValueBySize(fixture3Sizes, "XXL", fix3xxl, SpCustomerChoiceChannelFixtureSize::getInitialSetQty);
       assertEquals("Fixture 1 Initial Set Qty rollup should be sum of all size values", expectedTotalFix3InitialSetQty, (int) fixture3.getInitialSetQty());
    }

   @Test
   public void replenishmentCalculationTest() throws IOException, SizeAndPackException {
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
      //assertEquals(expectedTotalReplnUnits, (long) response.getMerchCatgReplPacks().get(0).getReplUnits());
      //assertEquals("Sum of all replns at size level equals total repln", expectedTotalReplnUnits, actualReplUnitsBySize);
   }

   @Test
   public void bumpSetCalculationTest() throws IOException, SizeAndPackException {
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

  /* @Test
   public void moveReplnToInitialSetWhenNoInitialSet()  {
      SizeDto size48 = size48();
      String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":5,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12309,\"replnWeekDesc\":\"FYE2024WK09\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":23}";
      BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
      Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
      Mockito.when(buyQtyProperties.getReplenishmentThreshold()).thenReturn(500);
      calculateFinelineBuyQuantity.updateQtysWithReplenishmentConstraints(entry);
      assertEquals("Entry BuyQtyObj totalReplenishment should be 0", 0, entry.getValue().getTotalReplenishment());
      assertEquals("Total Units of StoreQuantity IS Units should equal 23", 23.0, entry.getValue()
            .getBuyQtyStoreObj().getBuyQuantities().stream()
            .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0);
   }

   @Test
   public void moveReplnToInitialSetWhenInitialSet() {
      SizeDto size48 = size48();
      String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":1,\"totalUnits\":3,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":2,\"totalUnits\":4,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":5,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12309,\"replnWeekDesc\":\"FYE2024WK09\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":23}";
      BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
      Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
      Mockito.when(buyQtyProperties.getReplenishmentThreshold()).thenReturn(500);
      calculateFinelineBuyQuantity.updateQtysWithReplenishmentConstraints(entry);
      assertEquals("There should be only one StoreQuantity with 0 TotalUnits", 1, entry.getValue().getBuyQtyStoreObj().getBuyQuantities().stream().filter(sq -> sq.getTotalUnits() == 0).count());
      assertEquals("There should be 2 StoreQuantity with > 0 TotalUnits", 2, entry.getValue().getBuyQtyStoreObj().getBuyQuantities().stream().filter(sq -> sq.getTotalUnits() > 0).count());
      assertEquals("Total Units of StoreQuantity IS Units should equal 30", 30.0, entry.getValue()
            .getBuyQtyStoreObj().getBuyQuantities().stream()
            .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0);
   }

   @Test
   public void emptyBuyQtyStoreObjProperlyHandled() {
      String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":100,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":100,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":null}";
      BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
      Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48(), bqo);
      calculateFinelineBuyQuantity.updateQtysWithReplenishmentConstraints(entry);
      assertEquals("Total replenishments should remain 0 because replenishment wasn't distributed", 0, entry.getValue().getTotalReplenishment());
   }*/

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



   public void assertBumpSetStoreObject(Set<SpCustomerChoiceChannelFixtureSize> spCCFixSizes, long expectedTotalBumpPackUnits) {
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

   public String readJsonFileAsString(String fileName) throws IOException {
      return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
   }

   public CalculateBuyQtyRequest create(String channel, int lvl0Nbr, int lvl1Nbr, int lvl2Nbr, int lvl3Nbr, int lvl4Nbr, int finelineNbr) {
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

   public CalculateBuyQtyParallelRequest createFromRequest(CalculateBuyQtyRequest request) {
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
}
