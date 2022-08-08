package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FineLineReplenishmentRepository extends JpaRepository<FinelineReplPack, FinelineReplPackId> {

    @Query(value="select new com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO(msp.merchCatPlanId.planId, " +
            "msp.merchCatPlanId.lvl0Nbr, " +
            "ssp.lvl0Desc, " +
            "msp.merchCatPlanId.lvl1Nbr, " +
            "ssp.lvl1Desc, " +
            "msp.merchCatPlanId.lvl2Nbr, " +
            "ssp.lvl2Desc, " +
            "msp.merchCatPlanId.lvl3Nbr, " +
            "ssp.lvl3Desc, " +
            "mrp.vendorPackCnt as lvl3VenderPackCount, " +
            "mrp.whsePackCnt as lvl3WhsePackCount, " +
            "mrp.vnpkWhpkRatio as lvl3vnpkWhpkRatio, " +
            "ssp.subCatPlanId.lvl4Nbr, " +
            "ssp.lvl4Desc, " +
            "srp.vendorPackCnt as lvl4VenderPackCount, " +
            "srp.whsePackCnt as lvl4WhsePackCount, " +
            "srp.vnpkWhpkRatio as lvl4vnpkWhpkRatio, " +
            "fp.finelinePlanId.finelineNbr, " +
            "fp.finelineDesc, " +
            "fp.altFinelineName as finelineAltDesc, " +
            "frp.finalBuyUnits as finelineFinalBuyUnits, " +
            "frp.replUnits as finelineReplQty, " +
            "frp.vendorPackCnt as finelineVenderPackCount, " +
            "frp.whsePackCnt as finelineWhsePackCount, " +
            "frp.vnpkWhpkRatio as finelineVnpkWhpkRatio, " +
            "frp.replPackCnt as finelineReplPack) " +
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
            "MerchCatgReplPack mrp " +
            "ON " +
            "msp.merchCatPlanId.planId = mrp.merchCatgReplPackId.planId " +
            "AND msp.merchCatPlanId.lvl0Nbr = mrp.merchCatgReplPackId.repTLvl0 " +
            "AND msp.merchCatPlanId.lvl1Nbr = mrp.merchCatgReplPackId.repTLvl1 " +
            "AND msp.merchCatPlanId.lvl2Nbr = mrp.merchCatgReplPackId.repTLvl2 " +
            "AND msp.merchCatPlanId.lvl3Nbr = mrp.merchCatgReplPackId.repTLvl3 " +
            "AND msp.merchCatPlanId.channelId = mrp.merchCatgReplPackId.channelId " +
            "left join " +
            "SubCatgReplPack srp " +
            "ON " +
            "ssp.subCatPlanId.merchCatPlanId.planId = srp.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl0Nbr = srp.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl1Nbr = srp.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl2Nbr = srp.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl3Nbr = srp.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND ssp.subCatPlanId.lvl4Nbr = srp.subCatgReplPackId.repTLvl4 " +
            "AND ssp.subCatPlanId.merchCatPlanId.channelId = srp.subCatgReplPackId.merchCatgReplPackId.channelId " +
            "inner join " +
            "FinelineReplPack frp " +
            "ON " +
            "fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND fp.finelinePlanId.subCatPlanId.lvl4Nbr = frp.finelineReplPackId.subCatgReplPackId.repTLvl4 " +
            "AND fp.finelinePlanId.finelineNbr = frp.finelineReplPackId.finelineNbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId " +
            "where fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId) and msp.merchCatPlanId.planId = :planId and (frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId is NULL or frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId = :channelId) " )
    List<ReplenishmentResponseDTO> getByPlanChannel(@Param("planId") Long planId, @Param("channelId") Integer channelId);
}
