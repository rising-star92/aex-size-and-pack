package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SPFactoryServiceTest {

    @Mock
    CcPackOptimizationRepository ccPackOptimizationRepository;

    @InjectMocks
    BQFactoryMapper BQFactoryMapper;


    @Test
    void testSetFinelines() throws SizeAndPackException, IOException {
        BuyQtyRequest request = new BuyQtyRequest();
        request.setPlanId(1L);
        List<FactoryDTO> factoryDTOList = Arrays.asList(
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","92323","Factory Name"),
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","53534","Factory Name 2")
        );
        BuyQtyResponse bqResponse = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BQFactoryMapper.setFactoriesForFinelines(factoryDTOList,bqResponse);
        assertEquals(2, bqResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getMetrics().getFactories().size());
    }

    @Test
    void testSetCC() throws IOException {
        BuyQtyRequest request = new BuyQtyRequest();
        request.setPlanId(1L);
        List<FactoryDTO> factoryDTOList = Arrays.asList(
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","92323","Factory Name"),
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","53534","Factory Name 2")
        );
        BuyQtyResponse bqResponse = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BQFactoryMapper.setFactoriesForCCs(factoryDTOList,bqResponse);
        assertEquals(2, bqResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getMetrics().getFactories().size());
    }


}