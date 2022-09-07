package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FineLineReplenishmentRepository extends JpaRepository<FinelineReplPack, FinelineReplPackId> {

    @Query(value="select new com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO(frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId , " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 , " +
            "msp.lvl0Desc, " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 , " +
            "msp.lvl1Desc, " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 , " +
            "msp.lvl2Desc, " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 , " +
            "msp.lvl3Desc, " +
            "mrp.replUnits as lvl3ReplQty, " +
            "mrp.vendorPackCnt as lvl3VenderPackCount, " +
            "mrp.whsePackCnt as lvl3WhsePackCount, " +
            "mrp.vnpkWhpkRatio as lvl3vnpkWhpkRatio, " +
            "ssp.subCatPlanId.lvl4Nbr, " +
            "ssp.lvl4Desc, " +
            "srp.replUnits as lvl4ReplQty, " +
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
            "from FinelineReplPack frp " +
            "join " +
            "FinelinePlan fp " +
            "ON " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.repTLvl4 = fp.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND frp.finelineReplPackId.finelineNbr = fp.finelinePlanId.finelineNbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            " join " +
            "MerchCatgReplPack mrp " +
            "ON " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId = mrp.merchCatgReplPackId.planId " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 = mrp.merchCatgReplPackId.repTLvl0 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 = mrp.merchCatgReplPackId.repTLvl1 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 = mrp.merchCatgReplPackId.repTLvl2 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 = mrp.merchCatgReplPackId.repTLvl3 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId = mrp.merchCatgReplPackId.channelId " +
            " join " +
            "SubCatgReplPack srp " +
            "ON " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId = srp.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 = srp.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1= srp.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2= srp.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 = srp.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.repTLvl4= srp.subCatgReplPackId.repTLvl4 " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId = srp.subCatgReplPackId.merchCatgReplPackId.channelId " +
            " join " +
            "MerchCatPlan msp " +
            "ON " +
            "msp.merchCatPlanId.planId = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND msp.merchCatPlanId.lvl0Nbr = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND msp.merchCatPlanId.lvl1Nbr = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND msp.merchCatPlanId.lvl2Nbr =frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND msp.merchCatPlanId.lvl3Nbr = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND msp.merchCatPlanId.channelId = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId " +
            " join " +
            "SubCatPlan ssp " +
            "ON " +
            "frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId  = ssp.subCatPlanId.merchCatPlanId.planId " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0= ssp.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 = ssp.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 = ssp.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3= ssp.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.repTLvl4 = ssp.subCatPlanId.lvl4Nbr " +
            "AND frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId = ssp.subCatPlanId.merchCatPlanId.channelId " +
            "where fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId =:channelId and msp.merchCatPlanId.planId = :planId  " )
    List<ReplenishmentResponseDTO> getByPlanChannel(@Param("planId") Long planId, @Param("channelId") Integer channelId);

    @Query(value="select new com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO(msp.merchCatPlanId.planId, " +
            "fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId, " +
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
            "frp.finalBuyUnits as buyQty, " +
            "frp.replUnits as replnQty, " +
            "frp.replUnits as adjReplnQty, " +
            "fp.altFinelineName) " +
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
            "where ((fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId,3) or :channelId is NULL) and msp.merchCatPlanId.planId = :planId and (frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId is NULL or frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId = :channelId or :channelId is NULL)) ")
    List<BuyQntyResponseDTO> getBuyQntyByPlanChannelOnline(@Param("planId") Long planId, @Param("channelId") Integer channelId);
}
