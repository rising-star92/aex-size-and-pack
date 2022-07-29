package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpFineLineChannelFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FineLineReplenishmentRepository extends JpaRepository<SpFineLineChannelFixture, SpFineLineChannelFixtureId> {

    @Query(value="select new com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO(msp.merchCatPlanId.planId, " +
            "msp.merchCatPlanId.lvl0Nbr, " +
            "ssp.lvl0Desc, " +
            "msp.merchCatPlanId.lvl1Nbr, " +
            "ssp.lvl1Desc, " +
            "msp.merchCatPlanId.lvl2Nbr, " +
            "ssp.lvl2Desc, " +
            "msp.merchCatPlanId.lvl3Nbr, " +
            "ssp.lvl3Desc, " +
            "ssp.subCatPlanId.lvl4Nbr, " +
            "ssp.lvl4Desc, " +
            "fp.finelinePlanId.finelineNbr, " +
            "fp.finelineDesc, " +
            "fp.altFinelineName as finelineAltDesc, " +
            "frp.vendorPackCnt as finelineVenderPackCount, " +
            "frp.whsePackCnt as finelineWhsePackCount, " +
            "frp.vnpkWhpkRatio as finelineVnpkWhpkRatio, " +
            "frp.replPackCnt as finelineReplPack, " +
            "sfcf.buyQty as finelineFinalBuyUnits, " +
            "sfcf.replnQty as finelineReplQty) " +
            "from MerchCatPlan msp " +
            "inner join " +
            "SubCatPlan ssp " +
            "ON " +
            "msp.merchCatPlanId.planId = ssp.subCatPlanId.merchCatPlanId.planId " +
            "AND msp.merchCatPlanId.lvl0Nbr = ssp.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND msp.merchCatPlanId.lvl1Nbr = ssp.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND msp.merchCatPlanId.lvl2Nbr = ssp.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND msp.merchCatPlanId.lvl3Nbr = ssp.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND msp.merchCatPlanId.channelId = ssp.subCatPlanId.merchCatPlanId.channelId " +
            "inner join " +
            "FinelinePlan fp " +
            "ON " +
            "ssp.subCatPlanId.merchCatPlanId.planId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl0Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl1Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl2Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl3Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND ssp.subCatPlanId.lvl4Nbr = fp.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.channelId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "left join " +
            "FinelineReplenishmentPack frp " +
            "ON " +
            "fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId = frp.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.planId " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = frp.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl0 " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = frp.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl1 " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = frp.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl2 " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = frp.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl3 " +
            "AND fp.finelinePlanId.subCatPlanId.lvl4Nbr = frp.finelineReplenishmentPackId.subCatgReplenishmentPackId.repTLvl4 " +
            "AND fp.finelinePlanId.finelineNbr = frp.finelineReplenishmentPackId.finelineNbr " +
            "left join " +
            "SpFineLineChannelFixture sfcf " +
            "ON " +
            "fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sfcf.spFineLineChannelFixtureId.planId " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sfcf.spFineLineChannelFixtureId.lvl0Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sfcf.spFineLineChannelFixtureId.lvl1Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sfcf.spFineLineChannelFixtureId.lvl2Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sfcf.spFineLineChannelFixtureId.lvl3Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.lvl4Nbr = sfcf.spFineLineChannelFixtureId.lvl4Nbr " +
            "AND fp.finelinePlanId.finelineNbr = sfcf.spFineLineChannelFixtureId.fineLineNbr " +
            "where fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId) and msp.merchCatPlanId.planId = :planId and (sfcf.spFineLineChannelFixtureId.channelId is NULL or sfcf.spFineLineChannelFixtureId.channelId = :channelId) " )
    List<ReplenishmentResponseDTO> getByPlanChannel(@Param("planId") Long planId, @Param("channelId") Integer channelId);
}
