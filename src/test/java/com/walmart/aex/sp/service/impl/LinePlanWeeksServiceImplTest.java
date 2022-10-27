package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.bqfp.WeeksDTO;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.dto.lineplanner.*;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.service.GraphQLService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinePlanWeeksServiceImplTest {

    @InjectMocks
    private LinePlanWeeksServiceImpl linePlanWeeksService;

    @Mock
    private GraphQLService graphQLService;

    @Mock
    private GraphQLProperties graphQLProperties;

    @Test
    public void test_getWeeksByFinelineShouldReturnLinePlanWeeksResponse() throws SizeAndPackException {
        when(graphQLProperties.getBuyQtyLinePlanConsumerId()).thenReturn("testConsumerId");
        when(graphQLProperties.getBuyQtyLinePlanConsumerName()).thenReturn("testPlanConsumerName");
        when(graphQLProperties.getBuyQtyLinePlanConsumerEnv()).thenReturn("testENV");
        when(graphQLProperties.getLinePlanWeeksUrl()).thenReturn("testURL");
        when(graphQLProperties.getLinePlanWeeksQuery()).thenReturn("testQuery");
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getBuyQtyLinePlanConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getBuyQtyLinePlanConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getBuyQtyLinePlanConsumerEnv());
        Map<String, Object> data = new HashMap<>();
        data.put("planId", 1L);
        data.put("finelineIds", Collections.singletonList(1));
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
        when(graphQLService.post("testURL", "testQuery", headers, data))
                .thenReturn(response);
        GraphQLResponse actual = linePlanWeeksService.getWeeksByFineline(1, 1L, 0, 0);
        assertEquals(1, actual.getData().getGetLinePlanFinelines().get(0).getLvl4List().get(0)
                .getFinelines().get(0).getMetrics().getCurrent().getOnline().getFinancialAttributes().getTransactableStart().getWmYearWkLy());
        assertEquals(1, actual.getData().getGetLinePlanFinelines().get(0).getLvl4List().get(0)
                .getFinelines().get(0).getMetrics().getCurrent().getOnline().getFinancialAttributes().getTransactableEnd().getWmYearWkLy());
    }

    @Test
    public void test_getWeeksByFinelineShouldReturnNullWhenExceptionOccurs() throws SizeAndPackException {
        when(graphQLProperties.getBuyQtyLinePlanConsumerId()).thenReturn("testConsumerId");
        when(graphQLProperties.getBuyQtyLinePlanConsumerName()).thenReturn("testPlanConsumerName");
        when(graphQLProperties.getBuyQtyLinePlanConsumerEnv()).thenReturn("testENV");
        when(graphQLProperties.getLinePlanWeeksUrl()).thenReturn("testURL");
        when(graphQLProperties.getLinePlanWeeksQuery()).thenReturn("testQuery");
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getBuyQtyLinePlanConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getBuyQtyLinePlanConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getBuyQtyLinePlanConsumerEnv());
        Map<String, Object> data = new HashMap<>();
        data.put("planId", 1L);
        data.put("finelineIds", Collections.singletonList(1));
        when(graphQLService.post("testURL", "testQuery", headers, data)).thenThrow(SizeAndPackException.class);
        GraphQLResponse actual = linePlanWeeksService.getWeeksByFineline(1, 1L, 0, 0);
        assertNull(actual);

    }

}
