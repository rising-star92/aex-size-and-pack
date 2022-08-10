package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APRequest;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPRequest;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Mock
    SizeAndPackService sizeAndPackService;

    @Mock
    ObjectMapper objectMapper;

    @Before
    public void setUp() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(CalculateFinelineBuyQuantity.class, "objectMapper");
        field.setAccessible(true);
        field.set(calculateFinelineBuyQuantity, new ObjectMapper());
    }

    @Test
    public void calculateBuyQtyTest() throws SizeAndPackException, IOException {
        ObjectMapper objectMapper1 = new ObjectMapper();

        CalculateBuyQtyRequest calculateBuyQtyRequest = new CalculateBuyQtyRequest();
        calculateBuyQtyRequest.setPlanId(485L);
        calculateBuyQtyRequest.setChannel("store");
        calculateBuyQtyRequest.setLvl0Nbr(50000);
        calculateBuyQtyRequest.setLvl1Nbr(34);
        calculateBuyQtyRequest.setLvl2Nbr(1488);

        List<Lvl3Dto> lvl3DtoList = new ArrayList<>();
        Lvl3Dto lvl3Dto = new Lvl3Dto();
        lvl3Dto.setLvl3Nbr(9074);

        List<Lvl4Dto> lvl4DtoList = new ArrayList<>();
        Lvl4Dto lvl4Dto = new Lvl4Dto();
        lvl4Dto.setLvl4Nbr(7211);

        List<FinelineDto> finelineDtoList = new ArrayList<>();
        FinelineDto finelineDto = new FinelineDto();
        finelineDto.setFinelineNbr(572);
        finelineDtoList.add(finelineDto);

        lvl4Dto.setFinelines(finelineDtoList);
        lvl4DtoList.add(lvl4Dto);
        lvl3Dto.setLvl4List(lvl4DtoList);
        lvl3DtoList.add(lvl3Dto);
        calculateBuyQtyRequest.setLvl3List(lvl3DtoList);

        List<SpFineLineChannelFixture> spFineLineChannelFixtures1 = new ArrayList<>();

        Mockito.when(spFineLineChannelFixtureRepository.findSpFineLineChannelFixtureBySpFineLineChannelFixtureId_planIdAndSpFineLineChannelFixtureId_channelId(485L, 1))
                .thenReturn(java.util.Optional.of(spFineLineChannelFixtures1));

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(485L);
        buyQtyRequest.setChannel("store");
        buyQtyRequest.setLvl3Nbr(9074);
        buyQtyRequest.setLvl4Nbr(7211);
        buyQtyRequest.setFinelineNbr(572);

        BuyQtyResponse buyQtyResponse = objectMapper1.readValue(readJsonFileAsString("sizeProfileResponse"), BuyQtyResponse.class);
        Mockito.when(sizeAndPackService.getAllCcSizeProfiles(buyQtyRequest)).thenReturn(buyQtyResponse);

        APRequest apRequest = new APRequest();
        apRequest.setPlanId(485L);
        apRequest.setFinelineNbr(572);
        apRequest.setVolumeDeviationLevel("Fineline");

        APResponse apResponse = objectMapper1.readValue(readJsonFileAsString("rfaSizePackResponse"), APResponse.class);
        log.info("AP Response Test: {}", apResponse);
        Mockito.when(sizeAndPackService.fetchRunFixtureAllocationOutput(apRequest)).thenReturn(apResponse);

        BQFPRequest bqfpRequest = new BQFPRequest();
        bqfpRequest.setPlanId(485L);
        bqfpRequest.setChannel("store");
        bqfpRequest.setFinelineNbr(572);

        BQFPResponse bqfpResponse = objectMapper1.readValue(readJsonFileAsString("bqfpServiceResponse"), BQFPResponse.class);
        Mockito.when(bqfpService.getBuyQuantityUnits(bqfpRequest)).thenReturn(bqfpResponse);

        CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest = new CalculateBuyQtyParallelRequest();
        calculateBuyQtyParallelRequest.setPlanId(485L);
        calculateBuyQtyParallelRequest.setChannel("store");
        calculateBuyQtyParallelRequest.setLvl0Nbr(50000);
        calculateBuyQtyParallelRequest.setLvl1Nbr(34);
        calculateBuyQtyParallelRequest.setLvl2Nbr(1488);
        calculateBuyQtyParallelRequest.setLvl3Nbr(9074);
        calculateBuyQtyParallelRequest.setLvl4Nbr(7211);
        calculateBuyQtyParallelRequest.setFinelineNbr(572);

        List<MerchCatgReplPack> merchCatgReplPacks = new ArrayList<>();
        CalculateBuyQtyResponse calculateBuyQtyResponse = new CalculateBuyQtyResponse();
        calculateBuyQtyResponse.setSpFineLineChannelFixtures(spFineLineChannelFixtures1);
        calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);

        CalculateBuyQtyResponse calculateBuyQtyResponse1 = calculateFinelineBuyQuantity.calculateFinelineBuyQty(calculateBuyQtyRequest,calculateBuyQtyParallelRequest,calculateBuyQtyResponse);

        assertEquals(2,calculateBuyQtyResponse1.getSpFineLineChannelFixtures().size());
    }

    private String readTextFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".txt")));
    }

    private String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/"+fileName+".json")));
    }
}
