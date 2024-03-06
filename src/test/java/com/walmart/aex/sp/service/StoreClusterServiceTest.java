package com.walmart.aex.sp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class StoreClusterServiceTest {

    @InjectMocks
    private StoreClusterService storeClusterService;

    @Test
    public void testFetchPOStoreClusterGrouping() {
        // Arrange

        // Act
        Map<String, List<Integer>> storeCluster = storeClusterService.fetchPOStoreClusterGrouping("S1", "2025");

        // Assert
        assertNotNull(storeCluster);
        assertEquals(2, storeCluster.size());
    }

}
