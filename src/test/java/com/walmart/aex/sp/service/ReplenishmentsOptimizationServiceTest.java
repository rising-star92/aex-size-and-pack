package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.service.impl.DeptAdminRuleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
class ReplenishmentsOptimizationServiceTest {

    @InjectMocks
    private ReplenishmentsOptimizationService replenishmentsOptimizationService;

    @Mock
    private DeptAdminRuleServiceImpl deptAdminRuleService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void assertUpdatedReplenishmentWithDcInboundQtyRulesWithScenario1() {
        Integer store_channelId = 1;
        Integer online_channelId = 2;
        Integer lv1Number = 34;
        Long planId = 12L;
        Mockito.when(deptAdminRuleService.getReplenishmentThreshold(Mockito.anyLong(), Mockito.anyInt())).thenReturn(2500);
        assertEquals(getReplenishmentsObj(List.of(595L,0L,0L,0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(175L, 240L, 180L,0L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(1410L,0L,0L,0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(450L, 580L, 375L,1L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(1L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(2500L, 2500L, 2500L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(2500L, 2500L, 2500L)),5, store_channelId, lv1Number, planId));
        Mockito.when(deptAdminRuleService.getReplenishmentThreshold(Mockito.anyLong(), Mockito.anyInt())).thenReturn(500);
        assertEquals(getReplenishmentsObj(List.of(500L, 500L, 901L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L, 950L, 950L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(1000L, 950L, 950L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1000L, 950L, 950L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(500L,501L,1000L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L, 1000L, 1000L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(15L,0L,0L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L, 1L, 1L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(1000L,1001L,0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1000L, 1000L, 1L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(500L,497L,498L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(499L, 499L, 499L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(500L,0L,0L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(250L, 250L, 0L)),5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(0L,0L,750L,0L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(0L,0L,500L,250L)),5, store_channelId, lv1Number, planId));
        assertEquals(Collections.emptyList(), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(null, 5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(499L, 505L, 0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L, 499L, 499L)), 5, store_channelId, lv1Number, planId));
        assertEquals(getReplenishmentsObj(List.of(1000L, 950L, 950L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1000L, 950L, 950L)),5, online_channelId, null, null));
        assertEquals(getReplenishmentsObj(List.of(175L,240L,180L,0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(175L, 240L, 180L,0L)),5, online_channelId, null, null));
        assertEquals(getReplenishmentsObj(List.of(500L, 760L, 0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(10L, 300L, 950L)),5, store_channelId, lv1Number, planId));
    }

    @Test
    void unorderedReplenWeeksShouldBeProperlyOptimizedAndOrdered() throws JsonProcessingException {
        String replenObj = "[{\"replnWeek\":12418,\"replnWeekDesc\":\"FYE2025WK18\",\"adjReplnUnits\":200},{\"replnWeek\":12402,\"replnWeekDesc\":\"FYE2025WK02\",\"adjReplnUnits\":50},{\"replnWeek\":12406,\"replnWeekDesc\":\"FYE2025WK06\",\"adjReplnUnits\":0}]";
        Mockito.when(deptAdminRuleService.getReplenishmentThreshold(Mockito.anyLong(), Mockito.anyInt())).thenReturn(750);
        List<Replenishment> replens = Arrays.asList(objectMapper.readValue(replenObj, Replenishment[].class));
        replens = replenishmentsOptimizationService.getUpdatedReplenishmentsPack(replens, 12,1,23, 123L);
        List<Integer> expectedWeekOrder = Arrays.asList(12402,12406,12418);
        assertEquals(expectedWeekOrder, replens.stream().map(Replenishment::getReplnWeek).collect(Collectors.toList()), "Should be in ascending order");
        assertEquals(12402, replens.stream()
              .filter(replenishment -> replenishment.getAdjReplnUnits() == 252)
              .findFirst().get().getReplnWeek(), "Week 12402 should have 252 units");
    }

    private List<Replenishment> getReplenishmentsObj(List<Long> longs) {
        List<Replenishment> replenishments = new ArrayList<>();
        longs.forEach(l -> {
            Replenishment replenishment = new Replenishment();
            replenishment.setAdjReplnUnits(l);
            replenishment.setReplnWeek(0);
            double noOfVendorPacks = Math.ceil((double) replenishment.getAdjReplnUnits() / 5.0);
            Long updatedAdjReplnUnit = (long) (noOfVendorPacks * 5.0);
            replenishment.setAdjReplnUnits(updatedAdjReplnUnit);
            replenishments.add(replenishment);
        });
        return replenishments;
    }
}
