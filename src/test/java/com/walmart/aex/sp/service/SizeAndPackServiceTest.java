package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
    private StrategyFetchService strategyFetchService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void fetchFinelineBuyQntyTest() throws IOException, SizeAndPackException
    {

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(471l, 1, 50000, null, 34, null, 6419,
                null, 12228, null, 31507, null, 2855, null,
                1125, 1125, 1125, null);

        BuyQntyResponseDTO buyQntyResponseDTO2 = new BuyQntyResponseDTO(471l, 1, 50000, null, 34, null, 6419,
                null, 12229, null, 31508, null, 2855, null,
                1125, 1125, 1125, null);

        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO(471l, 1, 50000, null, 34, null, 6419,
                null, 12229, null, 31508, null, 2760, null,
                1125, 1125, 1125, null);


        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        buyQntyResponseDTOS.add(buyQntyResponseDTO2);
        buyQntyResponseDTOS.add(buyQntyResponseDTO1);

        Mockito.when(spFineLineChannelFixtureRepository.getBuyQntyByPlanChannel(471l, 1)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(471l);
        buyQtyRequest.setChannel("Store");

        BuyQtyResponse buyQtyResponse1 = buyQtyResponseFromJson("/buyQtySizeResponse");
        Mockito.when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = sizeAndPackService.fetchFinelineBuyQnty(buyQtyRequest);
        assertEquals(471,buyQtyRequest.getPlanId());
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO,new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO2,new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTO1,new BuyQtyResponse(),null);

    }

    @Test
    public void fetchCcBuyQtyTest() throws IOException, SizeAndPackException {

        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12228, 31507, 2855, "34_2855_4_19_8", "34_2855_4_19_8_BLACK SOOT",
                1125, 1125, 1125, 1125,1125,1125,1);

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12228, 31507, 2855, "34_2855_4_19_8", "34_5471_3_24_001_CHINO TAN",
                1125, 1125, 1125, 1125,1125,1125,1);

        BuyQntyResponseDTO buyQntyResponseDTO2 = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12229, 31508, 2855, "34_2855_4_20_8", "34_2855_4_20_8_BLACK SOOT",
                1125, 1125, 1125, 1125,1125,1125,1);

        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        buyQntyResponseDTOS.add(buyQntyResponseDTO1);
        buyQntyResponseDTOS.add(buyQntyResponseDTO2);
        Mockito.when(spCustomerChoiceChannelFixtureRepository.getBuyQntyByPlanChannelFineline(471l, 1,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(471l);
        buyQtyRequest.setChannel("Store");
        buyQtyRequest.setFinelineNbr(2855);

        BuyQtyResponse buyQtyResponse1 = buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest,2855)).thenReturn(buyQtyResponse1);

        BuyQtyResponse buyQtyResponse = sizeAndPackService.fetchCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471,buyQtyRequest.getPlanId());

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO1,new BuyQtyResponse(),2855);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO2,new BuyQtyResponse(),2855);

        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTO,new BuyQtyResponse(),2855);
    }


    private String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }

    private BuyQtyResponse buyQtyResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BuyQtyResponse.class);
    }


}
