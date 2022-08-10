package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntegrationHubServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private IntegrationHubServiceProperties properties;

    private IntegrationHubService integrationHubService;

    @Mock
    private AnalyticsMlSendRepository analyticsMlSendRepository;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        integrationHubService = new IntegrationHubService(restTemplate, properties,analyticsMlSendRepository);
    }

    @Test
    public void successResponseReturnsFromIntegrationHub() {
        ResponseEntity<IntegrationHubResponseDTO> response = ResponseEntity.status(HttpStatus.OK).body(successResponse());
        when(properties.getUrl()).thenReturn("http://10.22.137.216/api/packopt?scenario=pack_optimization_dataproc");
        when(properties.getSizeAndPackUrl()).thenReturn("http://aex-size-and-pack.aex.dev.walmart.net");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(IntegrationHubResponseDTO.class))).thenReturn(response);
        RunPackOptResponse result = integrationHubService.callIntegrationHubForPackOpt(getRequest());
        assertNotNull(result);
    }

    private IntegrationHubResponseDTO successResponse() {
        IntegrationHubResponseDTO integrationHubResponseDTO = new IntegrationHubResponseDTO();
        integrationHubResponseDTO.setWf_running_id("433d3b");
        return integrationHubResponseDTO;
    }

    private RunPackOptRequest getRequest() {
        RunPackOptRequest request = new RunPackOptRequest();
        List<InputRequest> inputRequestList = new ArrayList<>();
        InputRequest inputRequest = new InputRequest();
        inputRequest.setFinelineNbr(15);
        inputRequest.setLvl0Nbr(10);
        inputRequest.setLvl1Nbr(11);
        inputRequest.setLvl2Nbr(12);
        inputRequest.setLvl3Nbr(13);
        inputRequest.setLvl4Nbr(14);
        InputRequest inputRequest2 = new InputRequest();
        inputRequest2.setFinelineNbr(25);
        inputRequest2.setLvl0Nbr(20);
        inputRequest2.setLvl1Nbr(21);
        inputRequest2.setLvl2Nbr(22);
        inputRequest2.setLvl3Nbr(23);
        inputRequest2.setLvl4Nbr(24);

        inputRequestList.add(inputRequest);
        inputRequestList.add(inputRequest2);
        request.setPlanId(13L);
        request.setRunUser("FirstName LastName");
        request.setInputRequest(inputRequestList);
        return request;
    }

}
