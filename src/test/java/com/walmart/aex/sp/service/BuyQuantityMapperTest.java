package com.walmart.aex.sp.service;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
public class BuyQuantityMapperTest {

    @InjectMocks
    BuyQuantityMapper buyQunatityMapper;
    @Mock
    BuyQntyResponseDTO buyQntyResponseDTO;

    @Mock
    StrategyFetchService strategyFetchService;
    private static final Integer finelineNbr=3470;
    private static final Long planId=471l;
    @Test
    public void testMapBuyQntyLvl2Sp() {

        BuyQtyResponse fetchFineLineResponse= new BuyQtyResponse();
        buyQntyResponseDTO=new BuyQntyResponseDTO();
        buyQntyResponseDTO.setChannelId(2);
        buyQntyResponseDTO.setPlanId(planId);
        buyQntyResponseDTO.setBuyQty(654);
        buyQntyResponseDTO.setBumpPackQty(12);
        buyQntyResponseDTO.setAdjReplnQty(234);
        buyQntyResponseDTO.setBumpPackQty(654);
        buyQntyResponseDTO.setCcBumpQty(64);
        buyQntyResponseDTO.setCcFlowStrategy(765);
        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,finelineNbr);
        assertNotNull(fetchFineLineResponse);
        assertEquals(fetchFineLineResponse.getPlanId(),471l);
    }

    @Test
    public void testMapBuyQntySizeSp() {
        List<SizeDto> sizeDtos = new ArrayList<>();
        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO();
        buyQntyResponseDTO.setAhsSizeId(1);
        buyQntyResponseDTO.setBuyQty(100);

        SizeDto sizeDto = new SizeDto();
        sizeDto.setAhsSizeId(1);
        MetricsDto metricsDto = new MetricsDto();
        sizeDto.setMetrics(metricsDto);
        sizeDtos.add(sizeDto);
        buyQunatityMapper.mapBuyQntySizeSp(Arrays.asList(buyQntyResponseDTO),sizeDtos.get(0));
        assertEquals(100,sizeDto.getMetrics().getBuyQty());

    }
}