package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ReplenishmentServiceTest {

    @Mock
    private  FineLineReplenishmentRepository fineLineReplenishmentRepository;

    @Mock
    private  SpCustomerChoiceReplenishmentRepository spCustomerChoiceReplenishmentRepository;

    @Mock
    private  SizeListReplenishmentRepository sizeListReplenishmentRepository;

    @Mock
    private  CatgReplnPkConsRepository catgReplnPkConsRepository;

    @Mock
    private  SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

    @Mock
    private  FinelineReplnPkConsRepository finelineReplnPkConsRepository;

    @Mock
    private  StyleReplnPkConsRepository styleReplnConsRepository;

    @Mock
    private  CcReplnPkConsRepository ccReplnConsRepository;

    @Mock
    private  CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    @Mock
    private  ReplenishmentMapper replenishmentMapper;

    @Mock
    private  UpdateReplnConfigMapper updateReplnConfigMapper;

    @Mock
    private ReplenishmentService replenishmentService;

    @Mock
    private BuyQtyCommonUtil buyQtyCommonUtil;

    @InjectMocks
    private ReplenishmentService replenishmentService1;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    @Mock
    private StrategyFetchService strategyFetchService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private BuyQtyResponseInputs buyQtyInputs;

    @Test
    public void updateVnpkWhpkForCatgReplnConsTest(){
        UpdateVnPkWhPkReplnRequest request = new UpdateVnPkWhPkReplnRequest();
        request.setPlanId(1L);
        request.setChannel("Store");
        request.setLvl3Nbr(3);
        request.setVnpk(1);
        request.setWhpk(1);
        replenishmentService.updateVnpkWhpkForCatgReplnCons(request);
        Mockito.verify(replenishmentService, Mockito.times(1)).updateVnpkWhpkForCatgReplnCons(request);
    }

    @Test
    public void fetchFinelineBuyQtyTest() throws IOException, SizeAndPackException {

        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInput();
        Mockito.when(fineLineReplenishmentRepository.getBuyQntyByPlanChannelOnline(471l, 2)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForOnline();
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyFinalResponse= new BuyQtyResponse();

        Mockito.when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineFinelineBuyQnty(buyQtyRequest);
        assertEquals(471,buyQtyRequest.getPlanId());
    }

    @Test
    public void fetchCcBuyQtyTest() throws IOException, SizeAndPackException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        Mockito.when(spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(471l, 2,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForOnline();
        buyQtyRequest.setFinelineNbr(2855);
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest,2855)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471,buyQtyRequest.getPlanId());
  }

    @Test
    public void fetchSizeBuyQtyTest() throws IOException, SizeAndPackException {

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(88L, 50000, 34, 6420,
                12238, 31526, 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN",3174,"L",
                1125, 1125, 1125);
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        Mockito.when(sizeListReplenishmentRepository.getSizeBuyQntyByPlanChannelOnlineCc(88L, 2,
                "34_5471_3_24_001_CHINO TAN")).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(88L);
        buyQtyRequest.setChannel("Online");
        buyQtyRequest.setCcId("34_5471_3_24_001_CHINO TAN");

        BuyQtyResponse buyQtyResponse = BuyQtyResponseInputs.buyQtyResponseFromJson("/sizeProfileResponse");

        Mockito.when(strategyFetchService.getBuyQtyResponseSizeProfile(buyQtyRequest)).thenReturn(buyQtyResponse);

        BuyQtyResponse buyQtyResponse1 = replenishmentService1.fetchOnlineSizeBuyQnty(buyQtyRequest);

        Mockito.verify(buyQuantityMapper, Mockito.times(5)).mapBuyQntySizeSp(Mockito.any(),Mockito.any());
    }


}