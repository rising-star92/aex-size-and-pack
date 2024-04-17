package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.StoreClusterMap;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.properties.StoreClusterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreClusterServiceImplTest {

    @Mock
    private GraphQLService graphQLService;

    @Mock
    private StoreClusterProperties storeClusterProperties;

    @Mock
    private GraphQLProperties graphQLProperties;

    @InjectMocks
    private StoreClusterServiceImpl storeClusterService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void testFetchPOStoreClusterGrouping() throws SizeAndPackException, IOException, IllegalAccessException {
        // Arrange
        File storeClusterInfoResponseFile = new File(Objects.requireNonNull(this.getClass()
                .getResource("/data/storeClusterResponse.json")).getFile());
        GraphQLResponse response = objectMapper.readValue(storeClusterInfoResponseFile, GraphQLResponse.class);

        Field storeClusterPropertiesField = ReflectionUtils.findField(StoreClusterServiceImpl.class, "storeClusterProperties");
        assert storeClusterPropertiesField != null;
        storeClusterPropertiesField.setAccessible(true);
        storeClusterPropertiesField.set(storeClusterService, storeClusterProperties);

        Field graphQlPropertiesField = ReflectionUtils.findField(StoreClusterServiceImpl.class, "graphQLProperties");
        assert graphQlPropertiesField != null;
        graphQlPropertiesField.setAccessible(true);
        graphQlPropertiesField.set(storeClusterService, graphQLProperties);

        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap()))
                .thenReturn(response);
        when(storeClusterProperties.isPOStoreClusterEnabled()).thenReturn(true);
        when(storeClusterProperties.getStoreClusterUrl()).thenReturn("http://localhost:8080/graphql");

        // Act
        StoreClusterMap storeCluster = storeClusterService.fetchPOStoreClusterGrouping("S1", "2025");

        // Assert
        assertNotNull(storeCluster);
        assertEquals(2, storeCluster.size());
        assertEquals("offshore", storeCluster.getKey(1822));
        assertEquals("onshore", storeCluster.getKey(1));
    }

    @Test
    void testFetchPOStoreClusterGroupingDisabled() throws SizeAndPackException, IOException, IllegalAccessException {
        // Arrange
        Field storeClusterPropertiesField = ReflectionUtils.findField(StoreClusterServiceImpl.class, "storeClusterProperties");
        assert storeClusterPropertiesField != null;
        storeClusterPropertiesField.setAccessible(true);
        storeClusterPropertiesField.set(storeClusterService, storeClusterProperties);

        // Act
        StoreClusterMap storeCluster = storeClusterService.fetchPOStoreClusterGrouping("S1", "2025");

        // Assert
        assertNotNull(storeCluster);
        assertEquals(0, storeCluster.size());
    }

}
