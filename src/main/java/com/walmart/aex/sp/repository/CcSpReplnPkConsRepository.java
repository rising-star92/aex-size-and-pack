package com.walmart.aex.sp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPackId;

public interface CcSpReplnPkConsRepository extends JpaRepository <CcSpMmReplPack, CcSpMmReplPackId>{

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
}
