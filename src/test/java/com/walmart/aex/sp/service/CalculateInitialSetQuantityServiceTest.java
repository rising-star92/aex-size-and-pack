package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.InitialSet;
import com.walmart.aex.sp.dto.buyquantity.InitialSetQuantity;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateInitialSetQuantityServiceTest {

    CalculateInitialSetQuantityService calculateInitialSetQuantityService;
    SizeDto sizeDto;
    Cluster volumeCluster;
    RFASizePackData rfaSizePackData;

    @BeforeEach
    void setUp() {
        calculateInitialSetQuantityService = new CalculateInitialSetQuantityService();
        MetricsDto metricsDto = new MetricsDto();
        metricsDto.setSizeProfilePct(5.23);
        metricsDto.setAdjSizeProfilePct(4.9);

        sizeDto = new SizeDto();
        sizeDto.setSizeDesc("29X30");
        sizeDto.setAhsSizeId(9354);
        sizeDto.setMetrics(metricsDto);

        rfaSizePackData = new RFASizePackData();
        rfaSizePackData.setStore_cnt(300);
        rfaSizePackData.setFixture_group(0.25F);

        InitialSet initialSet = new InitialSet();
        volumeCluster = new Cluster();
        volumeCluster.setInitialSet(initialSet);
    }

    @Test
    void Test_calculateInitialSetQtyWithZeroInitialSet() {
        volumeCluster.getInitialSet().setInitialSetUnitsPerFix(0L);
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQty(sizeDto, volumeCluster, rfaSizePackData);

        assertEquals(0.0, initialSetQuantity.getIsQty());
        assertEquals(0.0, initialSetQuantity.getPerStoreQty());
    }

    @Test
    void Test_calculateInitialSetQtyWithInitialSet() {
        volumeCluster.getInitialSet().setInitialSetUnitsPerFix(100L);
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQty(sizeDto, volumeCluster, rfaSizePackData);

        assertEquals(300.0, initialSetQuantity.getIsQty());
        assertEquals(1.0, initialSetQuantity.getPerStoreQty());
    }

    @Test
    void Test_calculateInitialSetQtyV2WithZeroInitialSet() {
        volumeCluster.getInitialSet().setInitialSetUnitsPerFix(0L);
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQtyV2(sizeDto, volumeCluster, rfaSizePackData);

        assertEquals(0.0, initialSetQuantity.getIsQty());
        assertEquals(0.0, initialSetQuantity.getPerStoreQty());
    }

    @Test
    void Test_calculateInitialSetQtyV2WithInitialSet() {
        volumeCluster.getInitialSet().setInitialSetUnitsPerFix(100L);
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQtyV2(sizeDto, volumeCluster, rfaSizePackData);

        assertEquals(300.0, initialSetQuantity.getIsQty());
        assertEquals(1.0, initialSetQuantity.getPerStoreQty());
    }

    @Test
    void Test_calculateInitialSetQtyV2WithZeroSizePct() {
        sizeDto.getMetrics().setAdjSizeProfilePct(0.0);
        volumeCluster.getInitialSet().setInitialSetUnitsPerFix(100L);
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQtyV2(sizeDto, volumeCluster, rfaSizePackData);

        assertEquals(0.0, initialSetQuantity.getIsQty());
        assertEquals(0.0, initialSetQuantity.getPerStoreQty());
    }

    @Test
    void Test_calculateInitialSetQtyV2WithSizePct() {
        sizeDto.getMetrics().setAdjSizeProfilePct(0.2);
        volumeCluster.getInitialSet().setInitialSetUnitsPerFix(100L);
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQtyV2(sizeDto, volumeCluster, rfaSizePackData);

        assertEquals(300.0, initialSetQuantity.getIsQty());
        assertEquals(1.0, initialSetQuantity.getPerStoreQty());
    }
}