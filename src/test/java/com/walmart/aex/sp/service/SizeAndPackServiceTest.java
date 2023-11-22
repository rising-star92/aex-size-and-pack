package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.packDescription.PackDescriptionDetail;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDeleteDTO;
import com.walmart.aex.sp.dto.planhierarchy.SizeAndPackResponse;
import com.walmart.aex.sp.entity.CustChoicePlan;
import com.walmart.aex.sp.entity.FinelinePlan;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static com.walmart.aex.sp.util.BuyQtyResponseInputs.convertChannelToStore;
import static com.walmart.aex.sp.util.SizeAndPackTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SizeAndPackServiceTest {

    @Mock
    private SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    @Mock
    private SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;

    @InjectMocks
    private SizeAndPackService sizeAndPackService;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    @Mock
    private BuyQtyCommonUtil buyQtyCommonUtil;

    @Mock
    private StrategyFetchService strategyFetchService;

    @Mock
    private SizeAndPackDeleteService sizeAndPackDeleteService;
    @Mock
    private CustomerChoiceRepository customerChoiceRepository;
    @Mock
    private FinelinePlanRepository finelinePlanRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private static Integer fineline1Nbr = 151;
    private static String styleNbr = "151_2_23_001";
    private static String ccId = "151_2_23_001_001";
    @Test
    public void fetchFinelineBuyQntyTest() throws IOException, SizeAndPackException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInput();
        convertChannelToStore(buyQntyResponseDTOS);
        Mockito.when(spFineLineChannelFixtureRepository.getBuyQntyByPlanChannel(471l, 1)).thenReturn(buyQntyResponseDTOS);
        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForStore();
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = sizeAndPackService.fetchFinelineBuyQnty(buyQtyRequest);
        assertEquals(471,buyQtyRequest.getPlanId());
    }

    @Test
    public void fetchCcBuyQtyTest() throws IOException, SizeAndPackException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        convertChannelToStore(buyQntyResponseDTOS);
        Mockito.when(spCustomerChoiceChannelFixtureRepository.getBuyQntyByPlanChannelFineline(471l, 1,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForStore();
        buyQtyRequest.setFinelineNbr(2855);
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest,2855)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = sizeAndPackService.fetchCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471,buyQtyRequest.getPlanId());
    }

    @Test
    public void deleteSizeAndPackDataFinelineTest() throws SizeAndPackException{
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(fineline1Nbr,null,null);
        doNothing().when(sizeAndPackDeleteService).deleteSizeAndPackDataAtFl(Mockito.anyLong(), Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        Assert.assertEquals("Success",sizeAndPackResponse.getStatus());
    }

    @Test
    public void deleteSizeAndPackDataStyleTest() throws SizeAndPackException{
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(fineline1Nbr,styleNbr,null);
        doNothing().when(sizeAndPackDeleteService).deleteSizeAndPackDataAtStyleOrCC(Mockito.anyList(),Mockito.anyLong(), Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        Assert.assertEquals("Success",sizeAndPackResponse.getStatus());
    }
    @Test
    public void deleteSizeAndPackDataCCTest() throws SizeAndPackException{
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(fineline1Nbr,styleNbr,ccId);
        doNothing().when(sizeAndPackDeleteService).deleteSizeAndPackDataAtStyleOrCC(Mockito.anyList(),Mockito.anyLong(), Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        Assert.assertEquals("Success",sizeAndPackResponse.getStatus());
    }

    @Test
    public void deleteSizeAndPackDataFinelineTestWithNullStrongKey() throws SizeAndPackException{
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(null,null,null);
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        Assert.assertEquals("Failed",sizeAndPackResponse.getStatus());
    }

    @Test
    public void getPackDescriptionDetailsTestWithMultipleColors(){
        Long planId = 112l;
        Integer finelineNbr = 1283;
        Map<String, Set<String>> packIdCustomerChoiceMap = getPackIdCustomerChoiceMap();
        Set<CustChoicePlan> custChoicePlans = new HashSet<>();
        CustChoicePlan custChoicePlan1 = getCustChoicePlan();
        custChoicePlan1.getCustChoicePlanId().setCcId("34_1283_4_22_2_001");
        custChoicePlan1.setColorName("Blue");
        custChoicePlans.add(custChoicePlan1);
        CustChoicePlan custChoicePlan2 = getCustChoicePlan();
        custChoicePlan2.getCustChoicePlanId().setCcId("34_1283_4_22_2_002");
        custChoicePlan2.setColorName("Red");
        custChoicePlans.add(custChoicePlan2);
        FinelinePlan finelinePlan = getFinelinePlan();
        when(customerChoiceRepository.getCustomerChoicesByPlanIdFinelineNbrAndCc(planId,1,finelineNbr,getCustomerChoices())).thenReturn(custChoicePlans);
        when(finelinePlanRepository.findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(planId,finelineNbr,1)).thenReturn(Optional.of(finelinePlan));
        List<PackDescriptionDetail> result = sizeAndPackService.getPackDescriptionDetails(Math.toIntExact(planId),finelineNbr,packIdCustomerChoiceMap);
        Assert.assertEquals(1,result.size());
        Assert.assertEquals(null,result.get(0).getColor());
        Assert.assertEquals("Blue Soot Heather",result.get(0).getAltFinelineDesc().trim());
    }
    @Test
    public void getPackDescriptionDetailsTestWithOneColors(){
        Long planId = 112l;
        Integer finelineNbr = 1283;
        Map<String, Set<String>> packIdCustomerChoiceMap = getPackIdCustomerChoiceMap();
        Set<CustChoicePlan> custChoicePlans = new HashSet<>();
        CustChoicePlan custChoicePlan1 = getCustChoicePlan();
        custChoicePlan1.getCustChoicePlanId().setCcId("34_1283_4_22_2_001");
        custChoicePlan1.setColorName("Blue");
        custChoicePlans.add(custChoicePlan1);
        CustChoicePlan custChoicePlan2 = getCustChoicePlan();
        custChoicePlan2.getCustChoicePlanId().setCcId("34_1283_4_22_2_002");
        custChoicePlan2.setColorName("Blue");
        custChoicePlans.add(custChoicePlan2);
        FinelinePlan finelinePlan = getFinelinePlan();
        when(customerChoiceRepository.getCustomerChoicesByPlanIdFinelineNbrAndCc(planId,1,finelineNbr,getCustomerChoices())).thenReturn(custChoicePlans);
        when(finelinePlanRepository.findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(planId,finelineNbr,1)).thenReturn(Optional.of(finelinePlan));
        List<PackDescriptionDetail> result = sizeAndPackService.getPackDescriptionDetails(Math.toIntExact(planId),finelineNbr,packIdCustomerChoiceMap);
        Assert.assertEquals(1,result.size());
        Assert.assertEquals("Blue",result.get(0).getColor());
        Assert.assertEquals("Blue Soot Heather",result.get(0).getAltFinelineDesc().trim());
    }
    @Test
    public void getPackDescriptionDetailsTestWithNoColors(){
        Long planId = 112l;
        Integer finelineNbr = 1283;
        Map<String, Set<String>> packIdCustomerChoiceMap = getPackIdCustomerChoiceMap();
        Set<CustChoicePlan> custChoicePlans = new HashSet<>();
        CustChoicePlan custChoicePlan1 = getCustChoicePlan();
        custChoicePlan1.getCustChoicePlanId().setCcId("34_1283_4_22_2_001");
        custChoicePlan1.setColorName(null);
        custChoicePlans.add(custChoicePlan1);
        CustChoicePlan custChoicePlan2 = getCustChoicePlan();
        custChoicePlan2.getCustChoicePlanId().setCcId("34_1283_4_22_2_002");
        custChoicePlan2.setColorName(null);
        custChoicePlans.add(custChoicePlan2);
        FinelinePlan finelinePlan = getFinelinePlan();
        when(customerChoiceRepository.getCustomerChoicesByPlanIdFinelineNbrAndCc(planId,1,finelineNbr,getCustomerChoices())).thenReturn(custChoicePlans);
        when(finelinePlanRepository.findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(planId,finelineNbr,1)).thenReturn(Optional.of(finelinePlan));
        List<PackDescriptionDetail> result = sizeAndPackService.getPackDescriptionDetails(Math.toIntExact(planId),finelineNbr,packIdCustomerChoiceMap);
        Assert.assertEquals(1,result.size());
        Assert.assertEquals(null,result.get(0).getColor());
        Assert.assertEquals("Blue Soot Heather",result.get(0).getAltFinelineDesc().trim());
    }
    @Test
    public void getPackDescriptionDetailsTestWithNoAltFineline(){
        Long planId = 112l;
        Integer finelineNbr = 1283;
        Map<String, Set<String>> packIdCustomerChoiceMap = getPackIdCustomerChoiceMap();
        Set<CustChoicePlan> custChoicePlans = new HashSet<>();
        CustChoicePlan custChoicePlan1 = getCustChoicePlan();
        custChoicePlan1.getCustChoicePlanId().setCcId("34_1283_4_22_2_001");
        custChoicePlan1.setColorName("Blue");
        custChoicePlans.add(custChoicePlan1);
        CustChoicePlan custChoicePlan2 = getCustChoicePlan();
        custChoicePlan2.getCustChoicePlanId().setCcId("34_1283_4_22_2_002");
        custChoicePlan2.setColorName("Blue");
        custChoicePlans.add(custChoicePlan2);
        FinelinePlan finelinePlan = getFinelinePlan();
        finelinePlan.setAltFinelineName(null);
        when(customerChoiceRepository.getCustomerChoicesByPlanIdFinelineNbrAndCc(planId,1,finelineNbr,getCustomerChoices())).thenReturn(custChoicePlans);
        when(finelinePlanRepository.findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(planId,finelineNbr,1)).thenReturn(Optional.of(finelinePlan));
        List<PackDescriptionDetail> result = sizeAndPackService.getPackDescriptionDetails(Math.toIntExact(planId),finelineNbr,packIdCustomerChoiceMap);
        Assert.assertEquals(1,result.size());
        Assert.assertEquals("Blue",result.get(0).getColor());
        Assert.assertEquals(1,result.size());
        Assert.assertEquals(null,result.get(0).getAltFinelineDesc());
    }

    private Map<String, Set<String>> getPackIdCustomerChoiceMap() {
        Map<String, Set<String>> packIdCustomerChoiceMap = new HashMap<>();
        Set<String> customerChoice = getCustomerChoices();
        packIdCustomerChoiceMap.put("SP_is112_1283_0_nan_HANGING_0",customerChoice);
        return packIdCustomerChoiceMap;
    }

    private Set<String> getCustomerChoices() {
        Set<String> customerChoice = new HashSet<>();
        customerChoice.add("34_1283_4_22_2_001");
        customerChoice.add("34_1283_4_22_2_002");
        return customerChoice;
    }
}
