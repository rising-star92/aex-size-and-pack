package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.RfaWeeksResponse;
import com.walmart.aex.sp.dto.bqfp.WeeksDTO;
import com.walmart.aex.sp.dto.gql.GraphQLRfaResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BQFPServiceProperties;
import com.walmart.aex.sp.properties.GraphQLProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class RFAWeeksServiceTest {

    @InjectMocks
    private RFAWeeksService rfaWeeksService;

    @Mock
    private GraphQLService graphQLService;

    @Mock
    private GraphQLProperties graphQLProperties;

    @Mock
    private BQFPServiceProperties bqfpServiceProperties;

    @Test
    public void getRFAWeeksByFinelineTest() throws SizeAndPackException {
        GraphQLRfaResponse graphQLRfaResponseMock = new GraphQLRfaResponse();
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
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getAssortProductConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getAssortProductConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getAssortProductConsumerEnv());
        headers.put("wm_consumer.id", bqfpServiceProperties.getConsumerId());

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> variablevalues = new HashMap<>();
        variablevalues.put("planId", 1);
        variablevalues.put("finelineNbr", 1);
        variablevalues.put("lvl3Nbr", 1);
        variablevalues.put("lvl4Nbr", 1);
        data.put("rfaWeekRequest", variablevalues);
        Mockito.when(graphQLProperties.getRfaWeeksUrl()).thenReturn("");
        Mockito.when(graphQLProperties.getRfaWeeksQuery()).thenReturn("");

        Mockito.when(graphQLService.post("","", headers,data,GraphQLRfaResponse.class))
                .thenReturn(graphQLRfaResponseMock);
        GraphQLRfaResponse graphQLRfaResponse = rfaWeeksService.getRFAWeeksByFineline(1, 1, 1, 1);
        Assertions.assertNotNull(graphQLRfaResponse);
        RfaWeeksResponse rfaWeeksResp = graphQLRfaResponse.getData().getGetRFAWeeksByFineline();
        Assertions.assertNotNull(rfaWeeksResp);
        Assertions.assertEquals(rfaWeeksResp.getInStoreWeek().getWmYearWk(),1);
    }
}
