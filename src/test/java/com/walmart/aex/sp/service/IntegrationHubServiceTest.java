package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.FinelineDTO;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.Lvl3DTO;
import com.walmart.aex.sp.dto.packoptimization.Lvl4DTO;
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
        InputRequest inputRequest = new InputRequest();
        inputRequest.setLvl0Nbr(10);
        inputRequest.setLvl1Nbr(11);
        inputRequest.setLvl2Nbr(12);
        List<FinelineDTO> finelinesList = new ArrayList<>();
        FinelineDTO finelineDTO = new FinelineDTO();
        finelineDTO.setFinelineNbr(50);
        FinelineDTO finelineDTO1 = new FinelineDTO();
        finelineDTO1.setFinelineNbr(51);
        finelinesList.add(finelineDTO);
        finelinesList.add(finelineDTO1);
        List<Lvl4DTO> lvl4DTOS = new ArrayList<>();
        Lvl4DTO lvl4DTO = new Lvl4DTO();
        lvl4DTO.setLvl4Nbr(40);
        Lvl4DTO lvl4DTO1 = new Lvl4DTO();
        lvl4DTO.setLvl4Nbr(41);
        lvl4DTO.setFinelines(finelinesList);
        lvl4DTOS.add(lvl4DTO);
        List<Lvl3DTO> lvl3DTOS = new ArrayList<>();
        Lvl3DTO lvl3DTO = new Lvl3DTO();
        lvl3DTO.setLvl3Nbr(30);
        Lvl3DTO lvl3DTO1 = new Lvl3DTO();
        lvl3DTO.setLvl3Nbr(31);
        lvl3DTO.setLvl4DTOList(lvl4DTOS);
        lvl3DTOS.add(lvl3DTO);
        inputRequest.setLvl3List(lvl3DTOS);
        request.setPlanId(46L);
        request.setRunUser("Ravi Narayan Sukhasare");
        request.setInputRequest(inputRequest);

        return request;
    }

}
