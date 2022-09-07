package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private ReplenishmentService replenishmentService1;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

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
    public void fetchFinelineBuyQtyTest() {
        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(88L, 2, 50000, null, 34, null, 6420,
                null, 12238, null, 31526, null, 5471, null,
                1125, 1125, 1125, null);
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        Mockito.when(fineLineReplenishmentRepository.getBuyQntyByPlanChannelOnline(88L, 2)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(88L);
        buyQtyRequest.setChannel("Online");
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineFinelineBuyQnty(buyQtyRequest);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO,new BuyQtyResponse(),null);
    }

    @Test
    public void fetchCcBuyQtyTest() {
        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(88L, 50000, 34, 6420,
                12238, 31526, 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN",
                1125, 1125, 1125, 1125,1125,1125,2);
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        Mockito.when(spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(88L, 2,
                5471)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(88L);
        buyQtyRequest.setChannel("Online");
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineCcBuyQnty(buyQtyRequest, 5471);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO,new BuyQtyResponse(),5471);
    }

}