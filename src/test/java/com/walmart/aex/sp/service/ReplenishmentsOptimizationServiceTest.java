package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.util.AdjustedDCInboundQty;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Slf4j
public class ReplenishmentsOptimizationServiceTest {

    private ReplenishmentsOptimizationService replenishmentsOptimizationService;

    private AdjustedDCInboundQty adjustedDCInboundQty;

    @Before
    public void setUp() {
        replenishmentsOptimizationService = new ReplenishmentsOptimizationService(adjustedDCInboundQty);
    }
    @Test
    public void assertUpdatedReplenishmentWithDcInboundQtyRulesWithScenario1() {
        assertEquals(getReplenishmentsObj(List.of(595L,0L,0L,0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(175L, 240L, 180L,0L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(500L,906L,0L,0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(450L, 580L, 375L,1L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(1L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(500L, 500L, 500L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(500L, 500L, 500L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(500L, 500L, 901L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L, 950L, 950L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(1000L, 950L, 950L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1000L, 950L, 950L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(500L,501L,1000L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L, 1000L, 1000L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(3L,0L,0L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1L, 1L, 1L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(1000L,1001L,0L)), replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(1000L, 1000L, 1L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(500L,997l,0l)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(499L, 499L, 499L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(500L,0L,0L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(250L, 250L, 0L)),5.0));
        assertEquals(getReplenishmentsObj(List.of(0L,0L,750L,0L)),replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishmentsObj(List.of(0L,0L,500L,250L)),5.0));

    }
    private List<Replenishment> getReplenishmentsObj(List<Long> longs){
        List<Replenishment> replenishments = new ArrayList<>();
        longs.forEach(l -> {
            Replenishment replenishment = new Replenishment();
            replenishment.setAdjReplnUnits(l);
            int noOfVendorPacks = (int) Math.ceil(replenishment.getAdjReplnUnits() / 5.0);
            Long updatedAdjustedDcInboundQty = (long) (noOfVendorPacks * 5.0);
            replenishment.setDcInboundAdjUnits(updatedAdjustedDcInboundQty);
            replenishments.add(replenishment);
        });
        return replenishments;
    }
}
