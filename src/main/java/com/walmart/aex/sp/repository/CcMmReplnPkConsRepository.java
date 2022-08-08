package com.walmart.aex.sp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcMmReplPackId;

public interface CcMmReplnPkConsRepository extends JpaRepository <CcMmReplPack, CcMmReplPackId>{

	@Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "select * from dbo.rc_cc_mm_replpk_fixtr_cons where plan_id = :planId and \n"
            + "channel_id = :channelId and rpt_lvl_3_nbr=:lvl3Nbr and rpt_lvl_4_nbr=:lvl4Nbr and fineline_nbr=:fineline and style_nbr=:style and customer_choice=:customerChoice and merch_method_short_desc=:merchMethodDesc and fixturetype_rollup_id = :fixtureTypeRollupId", nativeQuery = true)
	List<CcMmReplPack> getCcMmReplnPkConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr, 
			@Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline, @Param("style") String style, @Param("customerChoice") String customerChoice, 
			@Param("merchMethodDesc") String merchMethodDesc, @Param("fixtureTypeRollupId") Integer fixtureTypeRollupId);
}
