package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyObj;
import com.walmart.aex.sp.dto.buyquantity.InitialSetWithReplnsConstraint;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StoreQuantity;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class BuyQuantityConstraintServiceTest {

    @Mock
    CalculateBumpPackQtyService calculateBumpPackQtyService;

    @Mock
    BuyQtyProperties buyQtyProperties;

    @InjectMocks
    BuyQuantityConstraintService buyQuantityConstraintService;

    @Spy
    ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        calculateBumpPackQtyService = new CalculateBumpPackQtyService();
        buyQuantityConstraintService = new BuyQuantityConstraintService(calculateBumpPackQtyService, buyQtyProperties);
    }

    @Test
    public void moveReplnToInitialSetWhenNoInitialSet()  {
        SizeDto size48 = size48();
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":5,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12309,\"replnWeekDesc\":\"FYE2024WK09\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":23}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
        Mockito.when(buyQtyProperties.getReplenishmentThreshold()).thenReturn(500);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment());
        assertEquals(0, entry.getValue().getTotalReplenishment(), "Entry BuyQtyObj totalReplenishment should be 0");
        assertEquals(23.0, entry.getValue()
                .getBuyQtyStoreObj().getBuyQuantities().stream()
                .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0, "Total Units of StoreQuantity IS Units should equal 23");
    }

    @Test
    public void moveReplnToInitialSetWhenInitialSet() {
        SizeDto size48 = size48();
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":1,\"totalUnits\":3,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":2,\"totalUnits\":4,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":5,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12309,\"replnWeekDesc\":\"FYE2024WK09\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":23}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
        Mockito.when(buyQtyProperties.getReplenishmentThreshold()).thenReturn(500);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment());
        assertEquals(1, entry.getValue().getBuyQtyStoreObj().getBuyQuantities().stream().filter(sq -> sq.getTotalUnits() == 0).count(), "There should be only one StoreQuantity with 0 TotalUnits");
        assertEquals(2, entry.getValue().getBuyQtyStoreObj().getBuyQuantities().stream().filter(sq -> sq.getTotalUnits() > 0).count(), "There should be 2 StoreQuantity with > 0 TotalUnits");
        assertEquals(30.0, entry.getValue()
                .getBuyQtyStoreObj().getBuyQuantities().stream()
                .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0, "Total Units of StoreQuantity IS Units should equal 30");
    }

    @Test
    public void emptyBuyQtyStoreObjProperlyHandled() {
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":100,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":100,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":null}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48(), bqo);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment());
        assertEquals(0, entry.getValue().getTotalReplenishment(), "Total replenishments should remain 0 because replenishment wasn't distributed");
    }

    @Test
    public void processReplnConstraintWithlessReplnTest()  {
        SizeDto size48 = size48();
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":1,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":1,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":2}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
        Mockito.when(buyQtyProperties.getReplenishmentThreshold()).thenReturn(500);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment());
        assertEquals(0, entry.getValue().getTotalReplenishment(), "Entry BuyQtyObj totalReplenishment should be 0");
        assertEquals(2.0, entry.getValue()
                .getBuyQtyStoreObj().getBuyQuantities().stream()
                .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0, "Total Units of StoreQuantity IS Units should equal 2");
    }

    @Test
    public void constraintProcessingShouldNotResultInNegativeReplenishmentWeekUnits() {
        String bqoJson = "{\"replenishments\":[{\"replnWeek\":12327,\"replnWeekDesc\":\"FYE2024WK27\",\"adjReplnUnits\":2375},{\"replnWeek\":12331,\"replnWeekDesc\":\"FYE2024WK31\",\"adjReplnUnits\":2899},{\"replnWeek\":12335,\"replnWeekDesc\":\"FYE2024WK35\",\"adjReplnUnits\":3094},{\"replnWeek\":12339,\"replnWeekDesc\":\"FYE2024WK39\",\"adjReplnUnits\":136}],\"totalReplenishment\":0}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        RFASizePackData rfaSizePackData = new RFASizePackData();
        rfaSizePackData.setStore_cnt(100);
        InitialSetWithReplnsConstraint setWithReplnsConstraint = buyQuantityConstraintService.getISWithMoreReplenConstraint(bqo, 800, rfaSizePackData);

        List<Replenishment> adjustedReplns = setWithReplnsConstraint.getReplnsWithUnits();
        assertTrue(adjustedReplns.stream().allMatch(replenishment -> replenishment.getAdjReplnUnits() >= 0), "Reduction of all repln units should not produce negative value");
    }

    private BuyQtyObj deserializeBuyQtyObj(String json) {
        try {
            return mapper.readValue(json, BuyQtyObj.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Assertions.fail("Something happened deserializing store object");
        }
        return null;
    }

    private SizeDto size48() {
        return createSize(4042, "48", 0.0, 0.024999);
    }

    private SizeDto createSize(Integer sizeId, String sizeDesc, Double sizeProfilePct, Double adjSizeProfilePct) {
        SizeDto size = new SizeDto();
        size.setSizeId(sizeId);
        size.setSizeDesc(sizeDesc);
        size.setMetrics(new MetricsDto());
        size.getMetrics().setAdjSizeProfilePct(adjSizeProfilePct);
        size.getMetrics().setSizeProfilePct(sizeProfilePct);
        return size;
    }
}
