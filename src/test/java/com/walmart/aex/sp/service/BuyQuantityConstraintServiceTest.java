package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyObj;
import com.walmart.aex.sp.dto.buyquantity.InitialSetWithReplnsConstraint;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StoreQuantity;
import com.walmart.aex.sp.service.impl.DeptAdminRuleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class BuyQuantityConstraintServiceTest {

    @Mock
    CalculateBumpPackQtyService calculateBumpPackQtyService;

    @InjectMocks
    BuyQuantityConstraintService buyQuantityConstraintService;

    @Spy
    ObjectMapper mapper = new ObjectMapper();

    @Mock
    DeptAdminRuleServiceImpl deptAdminRuleService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        buyQuantityConstraintService = new BuyQuantityConstraintService(calculateBumpPackQtyService);
    }

    @Test
    void moveReplnToInitialSetWhenNoInitialSet()  {
        SizeDto size48 = size48();
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":5,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12309,\"replnWeekDesc\":\"FYE2024WK09\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":23}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment(), 500);
        assertEquals(0, entry.getValue().getTotalReplenishment(), "Entry BuyQtyObj totalReplenishment should be 0");
        assertEquals(23.0, entry.getValue()
                .getBuyQtyStoreObj().getBuyQuantities().stream()
                .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0, "Total Units of StoreQuantity IS Units should equal 23");
    }

    @Test
    void moveReplnToInitialSetWhenInitialSet() {
        SizeDto size48 = size48();
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":1,\"totalUnits\":3,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":2,\"totalUnits\":4,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":5,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12309,\"replnWeekDesc\":\"FYE2024WK09\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12313,\"replnWeekDesc\":\"FYE2024WK13\",\"replnUnits\":null,\"adjReplnUnits\":6,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":23}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment(), 500);
        assertEquals(1, entry.getValue().getBuyQtyStoreObj().getBuyQuantities().stream().filter(sq -> sq.getTotalUnits() == 0).count(), "There should be only one StoreQuantity with 0 TotalUnits");
        assertEquals(2, entry.getValue().getBuyQtyStoreObj().getBuyQuantities().stream().filter(sq -> sq.getTotalUnits() > 0).count(), "There should be 2 StoreQuantity with > 0 TotalUnits");
        assertEquals(30.0, entry.getValue()
                .getBuyQtyStoreObj().getBuyQuantities().stream()
                .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0, "Total Units of StoreQuantity IS Units should equal 30");
    }

    @Test
    void emptyBuyQtyStoreObjProperlyHandled() {
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":100,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":100,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":null}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48(), bqo);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment(), 500);
        assertEquals(0, entry.getValue().getTotalReplenishment(), "Total replenishments should remain 0 because replenishment wasn't distributed");
    }

    @Test
    void processReplnConstraintWithlessReplnTest()  {
        SizeDto size48 = size48();
        String bqoJson = "{\"buyQtyStoreObj\":{\"buyQuantities\":[{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[4,5],\"sizeCluster\":1,\"volumeCluster\":2,\"bumpSets\":[],\"flowStrategyCode\":3},{\"isUnits\":0,\"totalUnits\":0,\"storeList\":[6,7,8,9,10],\"sizeCluster\":1,\"volumeCluster\":3,\"bumpSets\":[],\"flowStrategyCode\":3}]},\"replenishments\":[{\"replnWeek\":12301,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":1,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12305,\"replnWeekDesc\":\"FYE2024WK05\",\"replnUnits\":null,\"adjReplnUnits\":1,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}],\"totalReplenishment\":2}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        Map.Entry<SizeDto, BuyQtyObj> entry = new AbstractMap.SimpleEntry<>(size48, bqo);
        buyQuantityConstraintService.processReplenishmentConstraints(entry, bqo.getTotalReplenishment(), 500);
        assertEquals(0, entry.getValue().getTotalReplenishment(), "Entry BuyQtyObj totalReplenishment should be 0");
        assertEquals(2.0, entry.getValue()
                .getBuyQtyStoreObj().getBuyQuantities().stream()
                .mapToDouble(StoreQuantity::getTotalUnits).sum(), 0.0, "Total Units of StoreQuantity IS Units should equal 2");
    }

    @Test
    void constraintProcessingShouldNotResultInNegativeReplenishmentWeekUnits() {
        String bqoJson = "{\"replenishments\":[{\"replnWeek\":12327,\"replnWeekDesc\":\"FYE2024WK27\",\"adjReplnUnits\":2375},{\"replnWeek\":12331,\"replnWeekDesc\":\"FYE2024WK31\",\"adjReplnUnits\":2899},{\"replnWeek\":12335,\"replnWeekDesc\":\"FYE2024WK35\",\"adjReplnUnits\":3094},{\"replnWeek\":12339,\"replnWeekDesc\":\"FYE2024WK39\",\"adjReplnUnits\":136}],\"totalReplenishment\":0}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        RFASizePackData rfaSizePackData = new RFASizePackData();
        rfaSizePackData.setStore_cnt(100);
        buyQuantityConstraintService.getISWithMoreReplenConstraint(bqo, 800, rfaSizePackData, 2);

        List<Replenishment> adjustedReplns = bqo.getReplenishments();
        assertTrue(adjustedReplns.stream().allMatch(replenishment -> replenishment.getAdjReplnUnits() >= 0), "Reduction of all repln units should not produce negative value");
    }

    @Test
    void constraintProcessingShouldReduceRequiredCountFromWeekUnits() {
        String bqoJson = "{\"replenishments\":[{\"replnWeek\":12327,\"replnWeekDesc\":\"FYE2024WK27\",\"adjReplnUnits\":75},{\"replnWeek\":12331,\"replnWeekDesc\":\"FYE2024WK31\",\"adjReplnUnits\":300},{\"replnWeek\":12335,\"replnWeekDesc\":\"FYE2024WK35\",\"adjReplnUnits\":2899},{\"replnWeek\":12339,\"replnWeekDesc\":\"FYE2024WK39\",\"adjReplnUnits\":0}],\"totalReplenishment\":0}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        RFASizePackData rfaSizePackData = new RFASizePackData();
        rfaSizePackData.setStore_cnt(739);
        buyQuantityConstraintService.getISWithMoreReplenConstraint(bqo, 739, rfaSizePackData, 2);

        List<Replenishment> adjustedReplns = bqo.getReplenishments();
        assertTrue(adjustedReplns.stream().allMatch(replenishment -> replenishment.getAdjReplnUnits() >= 0), "Reduction of all repln units should not produce negative value");
    }

    @Test
    void constraintProcessingShouldSplitStoresWhenLessReplenishment() throws JsonProcessingException {
        String bqoJson = "{\"replenishments\":[{\"replnWeek\":12327,\"replnWeekDesc\":\"FYE2024WK27\",\"adjReplnUnits\":75},{\"replnWeek\":12331,\"replnWeekDesc\":\"FYE2024WK31\",\"adjReplnUnits\":300},{\"replnWeek\":12335,\"replnWeekDesc\":\"FYE2024WK35\",\"adjReplnUnits\":0},{\"replnWeek\":12339,\"replnWeekDesc\":\"FYE2024WK39\",\"adjReplnUnits\":0}],\"totalReplenishment\":0}";
        BuyQtyObj bqo = deserializeBuyQtyObj(bqoJson);
        RFASizePackData rfaSizePackData = new RFASizePackData();
        rfaSizePackData.setStore_cnt(408);
        rfaSizePackData.setVolume_group_cluster_id(1);
        rfaSizePackData.setSize_cluster_id(1);
        String storeListJson = "[3406, 1903, 3733, 6898, 256, 2688, 664, 410, 504, 2559, 5245, 3017, 535, 5703, 1341, 3294, 1464, 579, 5145, 2757, 529, 2990, 1241, 1720, 2322, 581, 3267, 2201, 829, 575, 5743, 1156, 2122, 365, 2928, 3763, 2399, 1535, 2857, 1514, 5056, 5027, 2037, 3461, 1348, 950, 3301, 2649, 2243, 536, 3630, 661, 2360, 1496, 1242, 844, 590, 5046, 2993, 2485, 5150, 1927, 3118, 2864, 3322, 2154, 3570, 601, 5748, 1923, 1313, 3237, 755, 3462, 3391, 5823, 1888, 3435, 4503, 1055, 2631, 3762, 3283, 1636, 1353, 1586, 1557, 1528, 5311, 622, 491, 3431, 2038, 2496, 645, 35, 1103, 870, 2700, 5480, 58, 2621, 4574, 1118, 1097, 1453, 1801, 3835, 3225, 1578, 4358, 258, 687, 4237, 781, 1014, 5247, 5705, 5087, 398, 500, 937, 4631, 3136, 400, 3209, 4298, 2049, 1737, 1483, 1229, 5491, 3284, 2755, 752, 896, 2197, 571, 3278, 1987, 5160, 5254, 3403, 1125, 892, 261, 1473, 821, 813, 284, 1454, 1, 1802, 5373, 184, 1671, 2616, 1752, 359, 1215, 228, 2283, 1644, 2987, 4440, 2712, 1217, 5479, 201, 2002, 2604, 753, 682, 1669, 2991, 2250, 878, 1488, 3056, 574, 2404, 2273, 851, 220, 699, 322, 1178, 1361, 2827, 4424, 3285, 824, 2117, 3460, 1376, 1355, 4274, 470, 818, 5080, 768, 1430, 3404, 2105, 2338, 3406, 1903, 3733, 6898, 256, 2688, 664, 410, 504, 2559, 5245, 3017, 535, 5703, 1341, 3294, 1464, 579, 5145, 2757, 529, 2990, 1241, 1720, 2322, 581, 3267, 2201, 829, 575, 5743, 1156, 2122, 365, 2928, 3763, 2399, 1535, 2857, 1514, 5056, 5027, 2037, 3461, 1348, 950, 3301, 2649, 2243, 536, 3630, 661, 2360, 1496, 1242, 844, 590, 5046, 2993, 2485, 5150, 1927, 3118, 2864, 3322, 2154, 3570, 601, 5748, 1923, 1313, 3237, 755, 3462, 3391, 5823, 1888, 3435, 4503, 1055, 2631, 3762, 3283, 1636, 1353, 1586, 1557, 1528, 5311, 622, 491, 3431, 2038, 2496, 645, 35, 1103, 870, 2700, 5480, 58, 2621, 4574, 1118, 1097, 1453, 1801, 3835, 3225, 1578, 4358, 258, 687, 4237, 781, 1014, 5247, 5705, 5087, 398, 500, 937, 4631, 3136, 400, 3209, 4298, 2049, 1737, 1483, 1229, 5491, 3284, 2755, 752, 896, 2197, 571, 3278, 1987, 5160, 5254, 3403, 1125, 892, 261, 1473, 821, 813, 284, 1454, 1, 1802, 5373, 184, 1671, 2616, 1752, 359, 1215, 228, 2283, 1644, 2987, 4440, 2712, 1217, 5479, 201, 2002, 2604, 753, 682, 1669, 2991, 2250, 878, 1488, 3056, 574, 2404, 2273, 851, 220, 699, 322, 1178, 1361, 2827, 4424, 3285, 824, 2117, 3460, 1376, 1355, 4274, 470, 818, 5080, 768, 1430, 3404, 2105, 2338]";
        List<Integer> storeList = Arrays.asList(mapper.readValue(storeListJson, Integer[].class));
        Cluster volumeCluster = new Cluster();
        volumeCluster.setFlowStrategy(1);
        SizeDto sizeDto = new SizeDto();

        when(calculateBumpPackQtyService.calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster, 35)).thenReturn(new ArrayList<>());
        InitialSetWithReplnsConstraint setWithReplnsConstraint = buyQuantityConstraintService.getISWithLessReplenConstraint(bqo, 375, storeList, 1.0, rfaSizePackData, volumeCluster, new SizeDto(), 2);

        StoreQuantity storeQuantityWithNoNewRep = setWithReplnsConstraint.getStoreQuantity();
        assertEquals(1.0, storeQuantityWithNoNewRep.getIsUnits(), "Stores should have old IS units");
        assertEquals(35.0, storeQuantityWithNoNewRep.getTotalUnits(), "35 stores should get 1 unit each");
        assertEquals(750.0, setWithReplnsConstraint.getIsQty(), "375 stores gets 2 units, IS will be 750");
        assertEquals(2.0, setWithReplnsConstraint.getPerStoreQty(), "New Per Store Qty after setting replenishments");
    }

    @Test
    void test_getTotalReplenishment() throws JsonProcessingException {
        String bqoJson = "[{\"replnWeek\":12327,\"replnWeekDesc\":\"FYE2024WK27\",\"adjReplnUnits\":75},{\"replnWeek\":12331,\"replnWeekDesc\":\"FYE2024WK31\",\"adjReplnUnits\":300},{\"replnWeek\":12335,\"replnWeekDesc\":\"FYE2024WK35\",\"adjReplnUnits\":2899},{\"replnWeek\":12339,\"replnWeekDesc\":\"FYE2024WK39\",\"adjReplnUnits\":0}]}";
        List<Replenishment> replenishments = Arrays.asList(mapper.readValue(bqoJson, Replenishment[].class));
        Long totalReplenishment = buyQuantityConstraintService.getTotalReplenishment(replenishments);

        assertEquals(3274, totalReplenishment, "Reduction of all repln units should not produce negative value");
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
