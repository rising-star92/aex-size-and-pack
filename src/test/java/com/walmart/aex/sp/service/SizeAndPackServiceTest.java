package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
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
import java.util.List;

import static com.walmart.aex.sp.util.BuyQtyResponseInputs.convertChannelToStore;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
    private BuyQtyResponseInputs buyQtyInputs;

    private final ObjectMapper mapper = new ObjectMapper();

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

}
