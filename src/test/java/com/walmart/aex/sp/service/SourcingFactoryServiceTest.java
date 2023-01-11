package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.Address;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsDTO;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsResponse;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import com.walmart.aex.sp.properties.SourcingFactoryServiceProperties;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SourcingFactoryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SourcingFactoryServiceProperties properties;

    @InjectMocks
    private SourcingFactoryService sourcingFactoryService;

    @Test
    void successResponseReturnsFromIntegrationHub() {
        MockitoAnnotations.openMocks(this);
        ResponseEntity<FactoryDetailsDTO> response = ResponseEntity.status(HttpStatus.OK).body(getFactoryDetails());
        when(properties.getUrl()).thenReturn("https://gs-factory-api.prod.us.walmart.net/factory.service.Api/Factorydetails/");
        when(properties.getEnv()).thenReturn("prod");
        when(properties.getServiceName()).thenReturn("SOURCING_FACTORY_API");
        when(properties.getConsumerId()).thenReturn("ea60868e-72a3-49cd-8f5a-bfabc98c9d3a");
        when(properties.getApiTokenKey()).thenReturn("V29vUGlnU29vaWUh");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(FactoryDetailsDTO.class))).thenReturn(response);
        FactoryDetailsResponse result = sourcingFactoryService.callSourcingFactoryForFactoryDetails("36161325");
        assertNotNull(result);
        assertEquals("FR",result.getCountryCode());
        assertEquals("FRANCE",result.getCountry());
        assertEquals("PKG--LA BC",result.getFactoryName());
    }

    @Test
    void failedResponseReturnsFromIntegrationHub() {
        MockitoAnnotations.openMocks(this);
        ResponseEntity<FactoryDetailsDTO> response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        when(properties.getUrl()).thenReturn("https://gs-factory-api.prod.us.walmart.net/factory.service.Api/Factorydetails/");
        when(properties.getEnv()).thenReturn("prod");
        when(properties.getServiceName()).thenReturn("SOURCING_FACTORY_API");
        when(properties.getConsumerId()).thenReturn("ea60868e-72a3-49cd-8f5a-bfabc98c9d3a");
        when(properties.getApiTokenKey()).thenReturn("V29vUGlnU29vaWUh");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(FactoryDetailsDTO.class))).thenReturn(response);
        FactoryDetailsResponse result = sourcingFactoryService.callSourcingFactoryForFactoryDetails("36161325");
        assertNull(result.getCountry());
        assertNull(result.getCountryCode());
        assertNull(result.getFactoryName());
    }
    private FactoryDetailsDTO getFactoryDetails() {
        FactoryDetailsDTO factoryDetailsDTO = new FactoryDetailsDTO();
        Address address = new Address();
        address.setCountry("FRANCE");
        address.setCountryCode("FR");
        factoryDetailsDTO.setFactoryId(36161325);
        factoryDetailsDTO.setFactoryName("PKG--LA BC");
        factoryDetailsDTO.setAddress(address);
        return factoryDetailsDTO;
    }
}
