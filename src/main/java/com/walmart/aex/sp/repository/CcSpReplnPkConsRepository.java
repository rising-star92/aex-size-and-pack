package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.packoptimization.DCInboundResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPackId;

import java.util.List;

public interface CcSpReplnPkConsRepository extends JpaRepository<CcSpMmReplPack, CcSpMmReplPackId> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update rc_cc_sp_mm_replpk_fixtr_cons set vendor_pack_cnt = :vnpk,whse_pack_cnt= :whpk, vnpk_whpk_ratio= :vnpkWhpkRatio, repl_pack_cnt = :replenishmentPackCount where plan_id = :plan_id and \n"
            + "channel_id = :channel_id and rpt_lvl_3_nbr=:rpt_lvl_3_nbr and rpt_lvl_4_nbr=:rpt_lvl_4_nbr and fineline_nbr=:fineline_nbr and style_nbr=:style_nbr and customer_choice=:customer_choice and merch_method_short_desc=:merchMethodDesc and ahs_size_id=:ahs_size_id and fixturetype_rollup_id = :fixtureTypeRollupId", nativeQuery = true)
    void updateSizeData(@Param("plan_id") Long plan_id, @Param("channel_id") Integer channel_id,
                        @Param("rpt_lvl_3_nbr") Integer rpt_lvl_3_nbr, @Param("rpt_lvl_4_nbr") Integer rpt_lvl_4_nbr,
                        @Param("fineline_nbr") Integer fineline_nbr, @Param("style_nbr") String style_nbr,
                        @Param("customer_choice") String customer_choice, @Param("ahs_size_id") Integer ahs_size_id, @Param("vnpk") Integer vnpk, @Param("whpk") Integer whpk,
                        @Param("vnpkWhpkRatio") Double vnpkWhpkRatio, @Param("replenishmentPackCount") Integer replenishmentPackCount,
                        @Param("merchMethodDesc") String merchMethodDesc, @Param("fixtureTypeRollupId") Integer fixtureTypeRollupId);

    @Query(value = "select new com.walmart.aex.sp.dto.packoptimization.DCInboundResponse(scp.subCatPlanId.merchCatPlanId.planId, scp.subCatPlanId.merchCatPlanId.lvl0Nbr, " +
            " scp.lvl0Desc , " +
            " scp.subCatPlanId.merchCatPlanId.lvl1Nbr , " +
            " scp.lvl1Desc , " +
            " scp.subCatPlanId.merchCatPlanId.lvl2Nbr , " +
            " scp.lvl2Desc, " +
            " scp.subCatPlanId.merchCatPlanId.lvl3Nbr , " +
            " scp.lvl3Desc, " +
            " scp.subCatPlanId.lvl4Nbr , " +
            " scp.lvl4Desc , " +
            " flp.finelinePlanId.finelineNbr , " +
            " flp.finelineDesc, " +
            " sp.stylePlanId.styleNbr , " +
            " csmrp.sizeDesc , " +
            " ccp.custChoicePlanId.ccId , " +
            " ccp.colorName , " +
            " csmrp.ccSpReplPackId.ahsSizeId , " +
            " csmrp.merchMethodDesc , " +
            " csmrp.replenObj ) " +
            "FROM SubCatPlan scp " +
            "INNER JOIN FinelinePlan flp " +
            "ON scp.subCatPlanId.merchCatPlanId.planId=flp.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND scp.subCatPlanId.merchCatPlanId.lvl0Nbr=flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND scp.subCatPlanId.merchCatPlanId.lvl1Nbr=flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND scp.subCatPlanId.merchCatPlanId.lvl2Nbr=flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND scp.subCatPlanId.merchCatPlanId.lvl3Nbr=flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND scp.subCatPlanId.lvl4Nbr=flp.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "INNER JOIN StylePlan sp " +
            "ON flp.finelinePlanId.subCatPlanId.merchCatPlanId.planId=sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr=sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr=sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr=sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr=sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND flp.finelinePlanId.subCatPlanId.lvl4Nbr=sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND flp.finelinePlanId.finelineNbr=sp.stylePlanId.finelinePlanId.finelineNbr " +
            "INNER JOIN CustChoicePlan ccp " +
            "ON sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId=ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId  " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr=ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr=ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr  " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr=ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr  " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr=ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr  " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr=ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND sp.stylePlanId.finelinePlanId.finelineNbr=ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr " +
            "INNER JOIN CcSpMmReplPack csmrp " +
            "ON ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.repTLvl4 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.styleNbr " +
            "AND ccp.custChoicePlanId.ccId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.customerChoice " +
            "WHERE scp.subCatPlanId.merchCatPlanId.planId=:planId GROUP BY " +
            "scp.subCatPlanId.merchCatPlanId.planId ,scp.subCatPlanId.merchCatPlanId.lvl0Nbr ,scp.lvl0Desc ,scp.subCatPlanId.merchCatPlanId.lvl1Nbr ,scp.lvl1Desc ,scp.subCatPlanId.merchCatPlanId.lvl2Nbr ,scp.lvl2Desc ," +
            "scp.subCatPlanId.merchCatPlanId.lvl3Nbr , scp.lvl3Desc, scp.subCatPlanId.lvl4Nbr ,scp.lvl4Desc, flp.finelinePlanId.finelineNbr ,flp.finelineDesc, sp.stylePlanId.styleNbr , csmrp.sizeDesc, " +
            "ccp.custChoicePlanId.ccId, csmrp.merchMethodDesc , ccp.colorName , " +
            " csmrp.ccSpReplPackId.ahsSizeId , " +
            " csmrp.merchMethodDesc , " +
            " csmrp.replenObj")
    List<DCInboundResponse> getDCInboundsByPlanId(@Param("planId") Long planId);
}
