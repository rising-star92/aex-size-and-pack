package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.bqfp.RfaWeeksResponse;
import com.walmart.aex.sp.dto.bqfp.WeeksDTO;
import com.walmart.aex.sp.dto.gql.Error;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.dto.historicalmetrics.WeeksResponse;
import com.walmart.aex.sp.dto.currentlineplan.*;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.service.ChannelWeeksService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeksServiceImplTest {

    @InjectMocks
    private WeeksServiceImpl weeksService;

    @Mock
    @Qualifier("rfaWeekService")
    private ChannelWeeksService rfaWeeksService;

    @Mock
    @Qualifier("linePlanWeekService")
    private ChannelWeeksService linePlanWeeksService;

    @Test
    void test_getWeeksShouldReturnStoreReponse() {
        GraphQLResponse graphQLRfaResponseMock = new GraphQLResponse();
        Payload payload = new Payload();
        RfaWeeksResponse rfaWeeksResponse = new RfaWeeksResponse();
        WeeksDTO inStoreweeksDTO = new WeeksDTO();
        inStoreweeksDTO.setWmYearWk(1);
        inStoreweeksDTO.setWmYearWkLly(1);
        WeeksDTO markWeeksDTO = new WeeksDTO();
        markWeeksDTO.setWmYearWkLly(1);
        markWeeksDTO.setWmYearWkLly(1);
        rfaWeeksResponse.setInStoreWeek(inStoreweeksDTO);
        rfaWeeksResponse.setMarkDownWeek(markWeeksDTO);
        payload.setGetRFAWeeksByFineline(rfaWeeksResponse);
        graphQLRfaResponseMock.setData(payload);
        when(rfaWeeksService.getWeeksByFineline(1,1L,1,1)).thenReturn(graphQLRfaResponseMock);
        WeeksResponse store = weeksService.getWeeks("Store", 1, 1L, 1, 1);
        assertEquals(1, store.getStartWeek().getWmYearWkLly());
        assertEquals(1, store.getEndWeek().getWmYearWkLly());
    }

    @Test
    void test_getWeeksShouldReturnExceptionWhenResponseNull() {
        when(linePlanWeeksService.getWeeksByFineline(1,1L,1,1)).thenReturn(new GraphQLResponse());
        Assertions.assertThrows(CustomException.class, () -> {
            weeksService.getWeeks("Online", 1, 1L, 1, 1);
        });
    }

    @Test
    void test_getWeeksShouldReturnExceptionWhenErrors() {
        GraphQLResponse response = new GraphQLResponse();
        response.setErrors(List.of(new Error()));
        when(rfaWeeksService.getWeeksByFineline(1,1L,1,1)).thenReturn(response);
        Assertions.assertThrows(CustomException.class, () -> {
            weeksService.getWeeks("Store", 1, 1L, 1, 1);
        });
    }

    @Test
    void test_getWeeksShouldReturnOnlineReponse() {
        GraphQLResponse response = new GraphQLResponse();
        Payload payload = new Payload();
        WeeksDTO markWeeksDTO = new WeeksDTO();
        markWeeksDTO.setWmYearWkLy(1);
        markWeeksDTO.setWmYearWkLly(1);
        payload.setGetLinePlanFinelines(Collections.singletonList(Lvl3.builder()
                .lvl4List(Collections.singletonList(Lvl4.builder()
                        .finelines(Collections.singletonList(Fineline.builder()
                                .metrics(Metrics.builder()
                                        .current(new PlanAttributes(new Attributes(FinancialAttributes.builder()
                                                .transactableStart(markWeeksDTO)
                                                .transactableEnd(markWeeksDTO)
                                                .build())))
                                        .build())
                                .build()))
                        .build()))
                .build()
        ));
        response.setData(payload);
        when(linePlanWeeksService.getWeeksByFineline(1,1L,1,1)).thenReturn(response);
        WeeksResponse online = weeksService.getWeeks("Online", 1, 1L, 1, 1);
        assertEquals(1, online.getStartWeek().getWmYearWkLy());
        assertEquals(1, online.getEndWeek().getWmYearWkLy());
    }
}
