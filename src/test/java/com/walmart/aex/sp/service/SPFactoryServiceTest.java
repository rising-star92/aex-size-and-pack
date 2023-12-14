package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.APRequest;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.gql.Error;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
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
    SPFactoryService spFactoryService;


    @Test
    void testSetFinelines() throws SizeAndPackException, IOException {
        BuyQtyRequest request = new BuyQtyRequest();
        request.setPlanId(1L);
        when(ccPackOptimizationRepository.getFactoriesByPlanId(anyLong(),eq(null))).thenReturn(Arrays.asList(
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","92323","Factory Name"),
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","53534","Factory Name 2")
        ));
        BuyQtyResponse bqResponse = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        spFactoryService.setFactoriesForFinelines(request,bqResponse);
        assertEquals(2, bqResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getMetrics().getFactories().size());
    }

    @Test
    void testSetCC() throws SizeAndPackException, IOException {
        BuyQtyRequest request = new BuyQtyRequest();
        request.setPlanId(1L);
        when(ccPackOptimizationRepository.getFactoriesByPlanId(anyLong(),any())).thenReturn(Arrays.asList(
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","92323","Factory Name"),
                new FactoryDTO(2855,"34_2855_4_19_8","34_2855_4_19_8_BLACK SOOT","53534","Factory Name 2")
        ));
        BuyQtyResponse bqResponse = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        spFactoryService.setFactoriesForCCs(request,bqResponse);
        assertEquals(2, bqResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getMetrics().getFactories().size());
    }


}