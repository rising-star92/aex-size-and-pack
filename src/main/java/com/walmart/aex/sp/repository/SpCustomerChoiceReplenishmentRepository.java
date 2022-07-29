package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpCustomerChoiceReplenishmentRepository extends JpaRepository<SpCustomerChoiceChannelFixture, SpCustomerChoiceChannelFixtureId> {

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
            "sp.stylePlanId.styleNbr, " +
            "sscf.buyQty as styleFinalBuyUnits, " +
            "sscf.replnQty as styleReplQty, " +
            "srp.vendorPackCnt as styleVenderPackCount, " +
            "srp.whsePackCnt as styleWhsePackCount, " +
            "srp.vnpkWhpkRatio as styleVnpkWhpkRatio, " +
            "srp.replPackCnt as styleReplPack, " +
            "ccp.custChoicePlanId.ccId, " +
            "ccp.colorName as colorName, " +
            "sccf.buyQty as ccFinalBuyUnits, " +
            "sccf.replnQty as ccReplQty, " +
            "crp.vendorPackCnt as ccVenderPackCount, " +
            "crp.whsePackCnt as ccWhsePackCount, " +
            "crp.vnpkWhpkRatio as ccVnpkWhpkRatio, " +
            "crp.replPackCnt as ccReplPack) " +
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
            "inner join " +
            "StylePlan sp " +
            "ON " +
            "fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.lvl4Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND fp.finelinePlanId.finelineNbr = sp.stylePlanId.finelinePlanId.finelineNbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "inner join " +
            "CustChoicePlan ccp " +
            "ON " +
            "sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND sp.stylePlanId.finelinePlanId.finelineNbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr " +
            "AND sp.stylePlanId.styleNbr = ccp.custChoicePlanId.stylePlanId.styleNbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "left join " +
            "StyleReplenishmentPack srp " +
            "ON " +
            "sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = srp.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.planId " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = srp.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl0 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = srp.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl1 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = srp.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl2 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = srp.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl3 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = srp.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.repTLvl4 " +
            "AND sp.stylePlanId.finelinePlanId.finelineNbr = srp.styleReplenishmentPackId.finelineReplenishmentPackId.finelineNbr " +
            "AND sp.stylePlanId.styleNbr = srp.styleReplenishmentPackId.styleNbr " +
            "left join " +
            "CcReplenishmentPack crp " +
            "ON " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = crp.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = crp.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl0 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = crp.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl1 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = crp.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl2 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = crp.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl3 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = crp.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.repTLvl4 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = crp.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.finelineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr = crp.ccReplenishmentPackId.styleReplenishmentPackId.styleNbr " +
            "AND ccp.custChoicePlanId.ccId = crp.ccReplenishmentPackId.customerChoice " +
            "left join " +
            "SpStyleChannelFixture sscf " +
            "ON " +
            "sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr " +
            "AND sp.stylePlanId.finelinePlanId.finelineNbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr " +
            "AND sp.stylePlanId.styleNbr = sscf.spStyleChannelFixtureId.styleNbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId " +
            "left join " +
            "SpCustomerChoiceChannelFixture sccf " +
            "ON " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.styleNbr " +
            "AND ccp.custChoicePlanId.ccId = sccf.spCustomerChoiceChannelFixtureId.customerChoice " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId " +
            "where sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId,3) and ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId) and msp.merchCatPlanId.planId = :planId and " +
            "fp.finelinePlanId.finelineNbr = :finelineNbr and " +
            "(sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId is NULL or sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = :channelId) and " +
            "(sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId is NULL or sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = :channelId) ")
    List<ReplenishmentResponseDTO> getReplenishmentByPlanChannelFineline(@Param("planId") Long planId, @Param("channelId") Integer channelId,
                                                                   @Param("finelineNbr") Integer finelineNbr);
}
