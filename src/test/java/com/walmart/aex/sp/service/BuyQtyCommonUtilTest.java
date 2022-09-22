package com.walmart.aex.sp.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BuyQtyCommonUtilTest {

    @InjectMocks
    private BuyQtyCommonUtil buyQtyCommonUtil;

    @Mock
    private BuyQtyResponseInputs buyQtyInputs;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void filterFinelinesWithSizesTest() throws IOException {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInputForOnline();

        BuyQtyResponse buyQtyResponseFromStrategy = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyResponse = buyQtyCommonUtil.filterFinelinesWithSizes(buyQntyResponseDTOS,buyQtyResponseFromStrategy);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(0),new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(1),new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(2),new BuyQtyResponse(),null);

    }

    @Test
    public void filterStylesCcWithSizesTest() throws IOException {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInputForStore();

        BuyQtyResponse buyQtyResponseFromStrategy = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyResponse = buyQtyCommonUtil.filterStylesCcWithSizes(buyQntyResponseDTOS,buyQtyResponseFromStrategy,2855);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(0),new BuyQtyResponse(),2855);
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(1),new BuyQtyResponse(),2855);
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(2),new BuyQtyResponse(),2855);

    }

}
