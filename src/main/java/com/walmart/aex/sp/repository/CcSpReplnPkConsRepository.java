package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.packoptimization.DCInboundResponse;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CcSpReplnPkConsRepository extends JpaRepository<CcSpMmReplPack, CcSpMmReplPackId> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update rc_cc_sp_mm_replpk_fixtr_cons set vendor_pack_cnt = :vnpk,whse_pack_cnt= :whpk, vnpk_whpk_ratio= :vnpkWhpkRatio, repl_pack_cnt = :replenishmentPackCount where plan_id = :plan_id and \n"
            + "channel_id = :channel_id and rpt_lvl_3_nbr=:rpt_lvl_3_nbr and rpt_lvl_4_nbr=:rpt_lvl_4_nbr and fineline_nbr=:fineline_nbr and style_nbr=:style_nbr and customer_choice=:customer_choice and merch_method_short_desc=:merchMethodDesc and ahs_size_id=:ahs_size_id ", nativeQuery = true)
    void updateSizeData(@Param("plan_id") Long plan_id, @Param("channel_id") Integer channel_id,
                        @Param("rpt_lvl_3_nbr") Integer rpt_lvl_3_nbr, @Param("rpt_lvl_4_nbr") Integer rpt_lvl_4_nbr,
                        @Param("fineline_nbr") Integer fineline_nbr, @Param("style_nbr") String style_nbr,
                        @Param("customer_choice") String customer_choice,@Param("ahs_size_id") Integer ahs_size_id, @Param("vnpk") Integer vnpk, @Param("whpk") Integer whpk,
                        @Param("vnpkWhpkRatio") Double vnpkWhpkRatio, @Param("replenishmentPackCount") Integer replenishmentPackCount,
                        @Param("merchMethodDesc") String merchMethodDesc);

    @Query(value = "select * from dbo.rc_cc_sp_mm_replpk_fixtr_cons where plan_id = :planId and \n"
            + "channel_id = :channelId and rpt_lvl_3_nbr=:lvl3Nbr and rpt_lvl_4_nbr=:lvl4Nbr and fineline_nbr=:fineline and style_nbr=:style and customer_choice=:customerChoice and merch_method_short_desc=:merchMethodDesc and ahs_size_id=:ahs_size_id ", nativeQuery = true)
	List<CcSpMmReplPack> getCcSpMmReplnPkConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr, 
			@Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline, @Param("style") String style, @Param("customerChoice") String customerChoice, 
			@Param("merchMethodDesc") String merchMethodDesc, @Param("ahs_size_id") Integer ahs_size_id);
    
    @Query(value = "select  csmrp from CcSpMmReplPack csmrp where " +
            "csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId=:planId " +
            "and  csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr=:fineline " +
            "and  csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.customerChoice=:customerChoice " +
            "and  csmrp.sizeDesc=:sizeDesc " +
            "and csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId=1" +
            "")
    Optional<List<CcSpMmReplPack>> findCcSpMmReplnPkConsData(@Param("planId")Long planId, @Param("fineline") Integer fineline, @Param("customerChoice") String customerChoice,
                                                       @Param("sizeDesc") String sizeDesc);

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
            " COALESCE(flp.altFinelineName, flp.finelineDesc) , " +
            " COALESCE(stp.altStyleDesc ,csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.styleNbr) as styleNbr, " +
            " csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId , " +
            " ct.channelDesc , " +
            " csmrp.sizeDesc , " +
            " COALESCE(ccp.altCcDesc, csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.customerChoice) as customerChoice, " +
            " csmrp.ccSpReplPackId.ahsSizeId , " +
            " ccp.colorName , " +
            " ccp.colorFamilyDesc , " +
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
            "AND scp.subCatPlanId.merchCatPlanId.channelId=flp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "RIGHT JOIN CcSpMmReplPack csmrp " +
            "ON flp.finelinePlanId.subCatPlanId.merchCatPlanId.planId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND flp.finelinePlanId.subCatPlanId.lvl4Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.repTLvl4 " +
            "AND flp.finelinePlanId.finelineNbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr " +
            "AND flp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId " +
            "RIGHT JOIN ChannelText ct " +
            "ON ct.channelId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId " +
            "RIGHT JOIN StylePlan stp " +
            "ON stp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND stp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND stp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND stp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND stp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND stp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.repTLvl4 " +
            "AND stp.stylePlanId.finelinePlanId.finelineNbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr " +
            "AND stp.stylePlanId.styleNbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.styleNbr " +
            "AND stp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId " +
            "RIGHT JOIN CustChoicePlan ccp " +
            "ON ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.repTLvl4 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.styleNbr " +
            "AND ccp.custChoicePlanId.ccId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.customerChoice " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId=csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId " +
            "WHERE scp.subCatPlanId.merchCatPlanId.planId=:planId AND csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId=:channelId GROUP BY " +
            "scp.subCatPlanId.merchCatPlanId.planId ,scp.subCatPlanId.merchCatPlanId.lvl0Nbr ,scp.lvl0Desc ,scp.subCatPlanId.merchCatPlanId.lvl1Nbr ,scp.lvl1Desc ,scp.subCatPlanId.merchCatPlanId.lvl2Nbr ,scp.lvl2Desc ," +
            "scp.subCatPlanId.merchCatPlanId.lvl3Nbr , scp.lvl3Desc, scp.subCatPlanId.lvl4Nbr ,scp.lvl4Desc, flp.finelinePlanId.finelineNbr ,flp.finelineDesc ,flp.altFinelineName ,stp.altStyleDesc ,ccp.altCcDesc , csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.styleNbr , csmrp.sizeDesc, " +
            "csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.customerChoice, csmrp.merchMethodDesc , ccp.colorName , ccp.colorFamilyDesc , " +
            "csmrp.ccSpReplPackId.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId , " +
            " ct.channelDesc , " +
            " csmrp.ccSpReplPackId.ahsSizeId , " +
            " csmrp.merchMethodDesc , " +
            " csmrp.replenObj " +
            "order by scp.lvl0Desc, scp.lvl1Desc, scp.lvl2Desc, scp.lvl3Desc, scp.lvl4Desc, flp.finelinePlanId.finelineNbr, styleNbr, customerChoice")
    List<DCInboundResponse> getDCInboundsByPlanIdAndChannelId(@Param("planId") Long planId ,@Param("channelId") Integer channelId);

   @Transactional
   @Modifying(clearAutomatically = true, flushAutomatically = true)
   @Query(value = "delete from rc_cc_sp_mm_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and fineline_nbr in (:finelineNbrs)", nativeQuery = true)
   void deleteByPlanIdFinelineIdChannelId(@Param("planId") Long planId, @Param("channelId") Integer channelId, @Param("finelineNbrs") Set<Integer> finelineNbrs);

    @Query(value = "select * from dbo.rc_cc_sp_mm_replpk_fixtr_cons where plan_id = :planId and \n"
            + "channel_id = :channelId and rpt_lvl_3_nbr=:lvl3Nbr and rpt_lvl_4_nbr=:lvl4Nbr and fineline_nbr=:fineline and style_nbr=:style and customer_choice=:customerChoice and merch_method_code=:merch_method_code ", nativeQuery = true)
    List<CcSpMmReplPack> getCcSpMmReplnPkVendorPackAndWhsePackCount(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr,
                                                  @Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline, @Param("style") String style, @Param("customerChoice") String customerChoice,
                                                  @Param("merch_method_code") Integer merch_method_code);
}
