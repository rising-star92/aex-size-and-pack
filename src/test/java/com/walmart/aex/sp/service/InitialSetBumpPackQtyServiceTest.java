package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitSetBumpPackDTO;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitSetBumpPackData;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSizeId;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialSetBumpPackQtyServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private BigQueryInitSetBpPkQtyService bigQueryInitSetBpPkQtyService;
    @Spy
    private InitialSetBumpPackQtyMapper initSetBpPkQtyMapper;
    @Mock
    private SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;
    @InjectMocks
    private InitialSetBumpPackQtyService initialSetBumpPackQtyService;

    @Test
    void test_getInitSetBpPkByPlanFinelineShouldReturnResponseWithAhsId() throws JsonProcessingException {
        InitSetBumpPackData gcpResponse = new InitSetBumpPackData();
        gcpResponse.setInitSetBpPkQtyDTOList(new ArrayList<>(Arrays.asList(
                objectMapper.readValue("{\"planAndFineline\":\"69_468\",\"styleNbr\":\"34_468_3_24_001\",\"customerChoice\":\"34_468_3_24_001_004\",\"merchMethodDesc\":\"HANGING\",\"size\":\"L\",\"finalInitialSetQty\":17650,\"bumpPackQty\":0}", InitSetBumpPackDTO.class),
                objectMapper.readValue("{\"planAndFineline\":\"69_468\",\"styleNbr\":\"34_468_3_24_001\",\"customerChoice\":\"34_468_3_24_001_004\",\"merchMethodDesc\":\"HANGING\",\"size\":\"M\",\"finalInitialSetQty\":14660,\"bumpPackQty\":0}", InitSetBumpPackDTO.class),
                objectMapper.readValue("{\"planAndFineline\":\"69_468\",\"styleNbr\":\"34_468_3_24_001\",\"customerChoice\":\"34_468_3_24_001_004\",\"merchMethodDesc\":\"HANGING\",\"size\":\"S\",\"finalInitialSetQty\":11477,\"bumpPackQty\":0}", InitSetBumpPackDTO.class)
        )));

        when(bigQueryInitSetBpPkQtyService.fetchInitialSetBumpPackDataFromGCP(any())).thenReturn(gcpResponse);
        List<SpCustomerChoiceChannelFixtureSize> dbResponse = new ArrayList<>(Arrays.asList(
                new SpCustomerChoiceChannelFixtureSize(new SpCustomerChoiceChannelFixtureSizeId(null, 1446), null, "M", null, null, null, null, null, null, null, null, null, null),
                new SpCustomerChoiceChannelFixtureSize(new SpCustomerChoiceChannelFixtureSizeId(null, 1463), null, "L", null, null, null, null, null, null, null, null, null, null),
                new SpCustomerChoiceChannelFixtureSize(new SpCustomerChoiceChannelFixtureSizeId(null, 1446), null, "M", null, null, null, null, null, null, null, null, null, null),
                new SpCustomerChoiceChannelFixtureSize(new SpCustomerChoiceChannelFixtureSizeId(null, 1463), null, "L", null, null, null, null, null, null, null, null, null, null),
                new SpCustomerChoiceChannelFixtureSize(new SpCustomerChoiceChannelFixtureSizeId(null, 1429), null, "S", null, null, null, null, null, null, null, null, null, null),
                new SpCustomerChoiceChannelFixtureSize(new SpCustomerChoiceChannelFixtureSizeId(null, 1429), null, "S", null, null, null, null, null, null, null, null, null, null)
        ));
        when(spCustomerChoiceChannelFixtureSizeRepository.getSpCcChanFixtrDataByPlanFineline(anyLong(), anyInt()))
                .thenReturn(dbResponse);
        BuyQtyRequest request = objectMapper.readValue("{\"planId\":69,\"channel\":\"store\",\"finelineNbr\":468}", BuyQtyRequest.class);
        BuyQtyResponse actual = initialSetBumpPackQtyService.getInitSetBpPkByPlanFineline(request);
        assertEquals(3, actual.getLvl3List().iterator().next().getLvl4List().iterator().next().getFinelines().iterator().next().getStyles().iterator().next().getCustomerChoices().iterator().next().getMerchMethods().iterator().next().getSizes().size());
        actual.getLvl3List().iterator().next().getLvl4List().iterator().next().getFinelines().iterator().next().getStyles().iterator().next().getCustomerChoices().iterator().next().getMerchMethods().iterator().next().getSizes().forEach(val -> {
            assertNotNull(val.getAhsSizeId());
        });
    }
}
