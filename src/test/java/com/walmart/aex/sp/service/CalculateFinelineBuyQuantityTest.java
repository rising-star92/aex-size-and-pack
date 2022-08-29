package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Slf4j
public class CalculateFinelineBuyQuantityTest {
    @InjectMocks
    CalculateFinelineBuyQuantity calculateFinelineBuyQuantity;

    @Mock
    SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    @Mock
    BQFPService bqfpService;

    private CalculateOnlineFinelineBuyQuantity calculateOnlineFinelineBuyQuantity;

    @Mock
    StrategyFetchService strategyFetchService;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
       MockitoAnnotations.openMocks(this);
       calculateFinelineBuyQuantity = new CalculateFinelineBuyQuantity(bqfpService, mapper, new BuyQtyReplenishmentMapperService(), calculateOnlineFinelineBuyQuantity,strategyFetchService);
    }

    @Test
    public void initialSetCalculationTest() throws SizeAndPackException, IOException {
       final String path = "/plan72fineline1500";
       BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
       APResponse rfaResponse = apResponseFromJson(path.concat("/RFAResponse"));
       BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));
       Mockito.when(bqfpService.getBuyQuantityUnits(any())).thenReturn(bqfpResponse);
       Mockito.when(strategyFetchService.getAllCcSizeProfiles(any())).thenReturn(buyQtyResponse);
       Mockito.when(strategyFetchService.getAPRunFixtureAllocationOutput(any())).thenReturn(rfaResponse);
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
       assertSizeInitialSetValue(fixture1Sizes, "XS", fix1xs);
       assertSizeInitialSetValue(fixture1Sizes, "S", fix1s);
       assertSizeInitialSetValue(fixture1Sizes, "M", fix1m);
       assertSizeInitialSetValue(fixture1Sizes, "L", fix1l);
       assertSizeInitialSetValue(fixture1Sizes, "XL", fix1xl);
       assertSizeInitialSetValue(fixture1Sizes, "XXL", fix1xxl);
       assertEquals("Fixture 1 Initial Set Qty rollup should be sum of all size values", expectedTotalFix1InitialSetQty, (int)fixture1.getInitialSetQty());

       assertEquals("Fixture 1 Should have 6 sizes present", 6, fixture1Sizes.size());
       int fix3xs = 2392;
       int fix3s = 6235;
       int fix3m = 11050;
       int fix3l = 11977;
       int fix3xl = 7420;
       int fix3xxl = 4499;
       int expectedTotalFix3InitialSetQty = IntStream.of(fix3xs, fix3s, fix3m, fix3l, fix3xl, fix3xxl).sum();
       assertSizeInitialSetValue(fixture3Sizes, "XS", fix3xs);
       assertSizeInitialSetValue(fixture3Sizes, "S", fix3s);
       assertSizeInitialSetValue(fixture3Sizes, "M", fix3m);
       assertSizeInitialSetValue(fixture3Sizes, "L", fix3l);
       assertSizeInitialSetValue(fixture3Sizes, "XL", fix3xl);
       assertSizeInitialSetValue(fixture3Sizes, "XXL", fix3xxl);
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
      assertEquals(expectedTotalReplnUnits, (long) response.getMerchCatgReplPacks().get(0).getReplUnits());
      assertEquals("Sum of all replns at size level equals total repln", expectedTotalReplnUnits, actualReplUnitsBySize);
   }

   private void assertSizeInitialSetValue(Set<SpCustomerChoiceChannelFixtureSize> sizes, String sizeDesc, int expectedISQty) {
      sizes.stream().filter(spccFix -> spccFix.getAhsSizeDesc().equalsIgnoreCase(sizeDesc))
            .findFirst().ifPresentOrElse(spccFix -> assertFixtureSizeInitialSetValues(spccFix, expectedISQty), () -> Assert.fail(sizeDesc));
   }

   private void assertFixtureSizeInitialSetValues(SpCustomerChoiceChannelFixtureSize actual, int expectedISQty) {
      assertEquals(String.format("Size %s should have correct value", actual.getAhsSizeDesc()), expectedISQty, (int) actual.getInitialSetQty());
   }

   private BQFPResponse bqfpResponseFromJson(String path) throws IOException {
      return mapper.readValue(readJsonFileAsString(path), BQFPResponse.class);
   }

   private APResponse apResponseFromJson(String path) throws IOException {
      return mapper.readValue(readJsonFileAsString(path), APResponse.class);
   }

   private BuyQtyResponse buyQtyResponseFromJson(String path) throws IOException {
      return mapper.readValue(readJsonFileAsString(path), BuyQtyResponse.class);
   }

   private String readTextFileAsString(String fileName) throws IOException {
      return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".txt")));
   }

   private String readJsonFileAsString(String fileName) throws IOException {
      return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
   }

   private CalculateBuyQtyRequest create(String channel, int lvl0Nbr, int lvl1Nbr, int lvl2Nbr, int lvl3Nbr, int lvl4Nbr, int finelineNbr) {
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

   private CalculateBuyQtyParallelRequest createFromRequest(CalculateBuyQtyRequest request) {
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
