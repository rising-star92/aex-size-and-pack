package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSizeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SizeListReplenishmentRepository extends JpaRepository<SpCustomerChoiceChannelFixtureSize, SpCustomerChoiceChannelFixtureSizeId> {

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
            "ccp.custChoicePlanId.ccId as ccId, " +
            "ccp.colorName as colorName, " +
            "csrp.merchMethodDesc as merchMethod, " +
            "csrp.ccSpReplenishmentPackId.ahsSizeId as ahsSizeId, " +
            "sccfs.ahsSizeDesc as sizeDesc, " +
            "sccfs.buyQty as ccSpFinalBuyUnits, " +
            "sccfs.replnQty as ccSpReplQty, " +
            "csrp.vendorPackCnt as ccSpVenderPackCount, " +
            "csrp.whsePackCnt as ccSpWhsePackCount, " +
            "csrp.vnpkWhpkRatio as ccSpVnpkWhpkRatio, " +
            "csrp.replPackCnt as ccSpReplPack) " +
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
            "SpCustomerChoiceChannelFixtureSize sccfs " +
            "ON " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.styleNbr " +
            "AND ccp.custChoicePlanId.ccId = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.customerChoice " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId " +
            "left join " +
            "CcSpReplenishmentPack csrp " +
            "ON " +
            "sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.planId " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl0 " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl1 " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl2 " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.merchantPackOptimizationId.repTLvl3 " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.subCatgReplenishmentPackId.repTLvl4 " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.finelineReplenishmentPackId.finelineNbr " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.styleNbr = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.styleReplenishmentPackId.styleNbr " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.customerChoice = csrp.ccSpReplenishmentPackId.ccReplenishmentPackId.customerChoice " +
            "AND sccfs.spCustomerChoiceChannelFixtureSizeId.ahsSizeId = csrp.ccSpReplenishmentPackId.ahsSizeId " +
            "where sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId) and ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId) and msp.merchCatPlanId.planId = :planId and " +
            "fp.finelinePlanId.finelineNbr = :finelineNbr and " +
            "sp.stylePlanId.styleNbr = :styleNbr and " +
            "ccp.custChoicePlanId.ccId = :ccId and " +
            "(sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId is NULL or sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = :channelId) ")
    List<ReplenishmentResponseDTO> getReplenishmentPlanChannelFinelineCc(@Param("planId") Long planId, @Param("channelId") Integer channelId,
                                                          @Param("finelineNbr") Integer finelineNbr,@Param("styleNbr") String styleNbr, @Param("ccId") String ccId);

}
