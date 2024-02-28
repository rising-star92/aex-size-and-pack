package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.repository.common.BuyQuantityCommonRepository;
import com.walmart.aex.sp.repository.common.ReplenishmentCommonRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CalculateBuyQuantityServiceTest {
    @InjectMocks
    private CalculateBuyQuantityService calculateBuyQuantityService;

    @Mock
    FinelinePlanRepository finelinePlanRepository;

    @Mock
    ReplenishmentCommonRepository replenishmentCommonRepository;

    @Mock
    CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    @Mock
    CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
    @Mock
    CcReplnPkConsRepository ccReplnPkConsRepository;
    @Mock
    StyleReplnPkConsRepository styleReplenishmentRepository;
    @Mock
    FineLineReplenishmentRepository fineLineReplenishmentRepository;
    @Mock
    BuyQuantityCommonRepository buyQuantityCommonRepository;
    @Mock
    SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
    @Mock
    SpStyleChannelFixtureRepository spStyleChannelFixtureRepository;
    @Mock
    SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
    @Mock
    SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;
    @Mock
    MerchCatgReplPackRepository merchCatgReplPackRepository;
    @Mock
    CalculateFinelineBuyQuantity calculateFinelineBuyQuantity;
    @Mock
    CalculateFinelineBuyQuantityMapper calculateFinelineBuyQuantityMapper;
    @Mock
    BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService;

    @Test
    void calculateBuyQtySuccessTest() {
        CalculateBuyQtyRequest calculateBuyQtyRequest = getCalculateBuyQtyRequest();
        List<Integer> finelines = List.of(59, 2670);
        Optional<List<FinelinePlan>> finelinePlanList = getFinelinePlans();
        Mockito.when(finelinePlanRepository.findAllByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrIn(calculateBuyQtyRequest.getPlanId(), finelines)).thenReturn(finelinePlanList);
        Mockito.when(replenishmentCommonRepository.getCcSpReplnPkConsRepository()).thenReturn(ccSpReplnPkConsRepository);
        Mockito.when(replenishmentCommonRepository.getCcMmReplnPkConsRepository()).thenReturn(ccMmReplnPkConsRepository);
        Mockito.when(replenishmentCommonRepository.getCcReplnPkConsRepository()).thenReturn(ccReplnPkConsRepository);
        Mockito.when(replenishmentCommonRepository.getStyleReplenishmentRepository()).thenReturn(styleReplenishmentRepository);
        Mockito.when(replenishmentCommonRepository.getFineLineReplenishmentRepository()).thenReturn(fineLineReplenishmentRepository);
        Mockito.when(replenishmentCommonRepository.getMerchCatgReplPackRepository()).thenReturn(merchCatgReplPackRepository);

        Mockito.when(buyQuantityCommonRepository.getSpCustomerChoiceChannelFixtureSizeRepository()).thenReturn(spCustomerChoiceChannelFixtureSizeRepository);
        Mockito.when(buyQuantityCommonRepository.getSpCustomerChoiceChannelFixtureRepository()).thenReturn(spCustomerChoiceChannelFixtureRepository);
        Mockito.when(buyQuantityCommonRepository.getSpStyleChannelFixtureRepository()).thenReturn(spStyleChannelFixtureRepository);
        Mockito.when(buyQuantityCommonRepository.getSpFineLineChannelFixtureRepository()).thenReturn(spFineLineChannelFixtureRepository);

        List<MerchCatgReplPack> merchCatgReplPacks = new ArrayList<>();
        MerchCatgReplPack merchCatgReplPack = new MerchCatgReplPack();
        MerchCatgReplPackId merchCatgReplPackId = new MerchCatgReplPackId();
        merchCatgReplPackId.setRepTLvl3(3398);
        merchCatgReplPack.setMerchCatgReplPackId(merchCatgReplPackId);
        merchCatgReplPack.setReplPackCnt(100);
        merchCatgReplPack.setReplUnits(100);
        merchCatgReplPacks.add(merchCatgReplPack);

        List<SpFineLineChannelFixture> spFineLineChannelFixtureList = new ArrayList<>();
        SpFineLineChannelFixture spFineLineChannelFixture = new SpFineLineChannelFixture();
        SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId();
        spFineLineChannelFixtureId.setFineLineNbr(59);
        spFineLineChannelFixture.setSpFineLineChannelFixtureId(spFineLineChannelFixtureId);
        SpFineLineChannelFixture spFineLineChannelFixture1 = new SpFineLineChannelFixture();
        spFineLineChannelFixture.setInitialSetQty(100);
        spFineLineChannelFixture.setBumpPackQty(100);
        spFineLineChannelFixture.setReplnQty(100);
        SpFineLineChannelFixtureId spFineLineChannelFixtureId1 = new SpFineLineChannelFixtureId();
        spFineLineChannelFixtureId1.setFineLineNbr(2670);
        spFineLineChannelFixture1.setSpFineLineChannelFixtureId(spFineLineChannelFixtureId1);
        spFineLineChannelFixtureList.add(spFineLineChannelFixture);
        spFineLineChannelFixtureList.add(spFineLineChannelFixture1);

        Mockito.when(merchCatgReplPackRepository.findMerchCatgReplPackByMerchCatgReplPackId_planIdAndMerchCatgReplPackId_channelId(any(),any())).thenReturn(Optional.of(merchCatgReplPacks));
        Mockito.when(spFineLineChannelFixtureRepository.findSpFineLineChannelFixtureBySpFineLineChannelFixtureId_planIdAndSpFineLineChannelFixtureId_channelId(any(),any())).thenReturn(Optional.of(spFineLineChannelFixtureList));

        CalculateBuyQtyResponse calculateBuyQtyResponse = new CalculateBuyQtyResponse();
        Mockito.doNothing().when(calculateFinelineBuyQuantityMapper).resetToZeroSpFinelineFixtures(any(),any());
        Mockito.doNothing().when(buyQtyReplenishmentMapperService).resetToZeroMerchCatgReplPack(any(),any(),any());
        calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
        calculateBuyQtyResponse.setSpFineLineChannelFixtures(spFineLineChannelFixtureList);
        Mockito.when(calculateFinelineBuyQuantity.calculateFinelineBuyQtyV2(any(),any(),any())).thenReturn(calculateBuyQtyResponse);
        List<StatusResponse> responses = calculateBuyQuantityService.calculateBuyQuantity(calculateBuyQtyRequest);
        Assert.assertNotNull(responses);
        Assert.assertEquals(1,responses.size());
        Assert.assertEquals("Success",responses.get(0).getStatus());
        Assert.assertEquals("59, 2670",responses.get(0).getMessage());
    }

    @Test
    void calculateBuyQtyExceptionWhenNoFinelineInRequestTest() {
        CalculateBuyQtyRequest calculateBuyQtyRequest = getCalculateBuyQtyRequestWithNoFineline();
        assertThrows(CustomException.class, () -> calculateBuyQuantityService.calculateBuyQuantity(calculateBuyQtyRequest));
    }


    private Optional<List<FinelinePlan>> getFinelinePlans() {
        FinelinePlanId finelinePlanId = new FinelinePlanId() ;
        finelinePlanId.setFinelineNbr(2670);
        SubCatPlanId subCatPlanId = new SubCatPlanId();
        subCatPlanId.setLvl4Nbr(7225);
        MerchCatPlanId merchCatPlanId = new MerchCatPlanId();
        merchCatPlanId.setLvl3Nbr(3398);
        merchCatPlanId.setLvl0Nbr(5000);
        merchCatPlanId.setLvl1Nbr(34);
        merchCatPlanId.setLvl2Nbr(2999);
        subCatPlanId.setMerchCatPlanId(merchCatPlanId);
        finelinePlanId.setSubCatPlanId(subCatPlanId);
        FinelinePlan finelinePlan = new FinelinePlan();
        finelinePlan.setFinelinePlanId(finelinePlanId);
        List<FinelinePlan> finelinePlans = List.of(finelinePlan);
        return Optional.of(finelinePlans);
    }

    private CalculateBuyQtyRequest getCalculateBuyQtyRequest() {
        CalculateBuyQtyRequest calculateBuyQtyRequest = new CalculateBuyQtyRequest();
        calculateBuyQtyRequest.setPlanId(73l);
        calculateBuyQtyRequest.setChannel("store");
        calculateBuyQtyRequest.setLvl0Nbr(50000);
        calculateBuyQtyRequest.setLvl1Nbr(34);
        calculateBuyQtyRequest.setLvl2Nbr(2999);
        List<Lvl3Dto> lvl3Dtos = new ArrayList<>();
        Lvl3Dto lvl3Dto = new Lvl3Dto();
        lvl3Dto.setLvl3Nbr(6148);
        List<Lvl4Dto> lvl4Dtos = new ArrayList<>();
        Lvl4Dto lvl4Dto = new Lvl4Dto();
        lvl4Dto.setLvl4Nbr(7241);
        List<FinelineDto> finelineDtos = new ArrayList<>();
        FinelineDto finelineDto = new FinelineDto();
        finelineDto.setFinelineNbr(59);
        finelineDtos.add(finelineDto);
        lvl4Dto.setFinelines(finelineDtos);
        lvl4Dtos.add(lvl4Dto);
        lvl3Dto.setLvl4List(lvl4Dtos);
        lvl3Dtos.add(lvl3Dto);
        Lvl3Dto lvl3Dto1 = new Lvl3Dto();
        lvl3Dto1.setLvl3Nbr(3398);
        List<Lvl4Dto> lvl4Dtos1 = new ArrayList<>();
        Lvl4Dto lvl4Dto1 = new Lvl4Dto();
        lvl4Dto1.setLvl4Nbr(7225);
        List<FinelineDto> finelineDtos1 = new ArrayList<>();
        FinelineDto finelineDto1 = new FinelineDto();
        finelineDto1.setFinelineNbr(2670);
        finelineDtos1.add(finelineDto1);
        lvl4Dto1.setFinelines(finelineDtos1);
        lvl4Dtos1.add(lvl4Dto1);
        lvl3Dto1.setLvl4List(lvl4Dtos1);
        lvl3Dtos.add(lvl3Dto1);
        calculateBuyQtyRequest.setLvl3List(lvl3Dtos);
        calculateBuyQtyRequest.setFiscalYear(2025);
        calculateBuyQtyRequest.setSeasonCode("S3");
        return calculateBuyQtyRequest;
    }

    private CalculateBuyQtyRequest getCalculateBuyQtyRequestWithNoFineline() {
        CalculateBuyQtyRequest calculateBuyQtyRequest = new CalculateBuyQtyRequest();
        calculateBuyQtyRequest.setPlanId(73l);
        calculateBuyQtyRequest.setChannel("store");
        calculateBuyQtyRequest.setLvl0Nbr(50000);
        calculateBuyQtyRequest.setLvl1Nbr(34);
        calculateBuyQtyRequest.setLvl2Nbr(2999);
        List<Lvl3Dto> lvl3Dtos = new ArrayList<>();
        Lvl3Dto lvl3Dto = new Lvl3Dto();
        lvl3Dto.setLvl3Nbr(6148);
        List<Lvl4Dto> lvl4Dtos = new ArrayList<>();
        Lvl4Dto lvl4Dto = new Lvl4Dto();
        lvl4Dto.setLvl4Nbr(7241);
        lvl4Dtos.add(lvl4Dto);
        lvl3Dto.setLvl4List(lvl4Dtos);
        lvl3Dtos.add(lvl3Dto);
        calculateBuyQtyRequest.setLvl3List(lvl3Dtos);
        calculateBuyQtyRequest.setFiscalYear(2025);
        calculateBuyQtyRequest.setSeasonCode("S3");
        return calculateBuyQtyRequest;
    }

}
