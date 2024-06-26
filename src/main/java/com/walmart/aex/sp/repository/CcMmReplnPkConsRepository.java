package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcMmReplPackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CcMmReplnPkConsRepository extends JpaRepository <CcMmReplPack, CcMmReplPackId>{

    @Query(value = "select * from dbo.rc_cc_mm_replpk_fixtr_cons where plan_id = :planId and \n"
            + "channel_id = :channelId and rpt_lvl_3_nbr=:lvl3Nbr and rpt_lvl_4_nbr=:lvl4Nbr and fineline_nbr=:fineline and style_nbr=:style and customer_choice=:customerChoice and merch_method_short_desc=:merchMethodDesc ", nativeQuery = true)
	List<CcMmReplPack> getCcMmReplnPkConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr, 
			@Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline, @Param("style") String style, @Param("customerChoice") String customerChoice, 
			@Param("merchMethodDesc") String merchMethodDesc);

	@Query(value = "select  csmrp from CcMmReplPack csmrp where " +
			"csmrp.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId=:planId " +
			"and  csmrp.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr=:fineline " +
			"and  csmrp.ccMmReplPackId.ccReplPackId.customerChoice=:customerChoice " +
			"and csmrp.ccMmReplPackId.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId=1" +
			"")
	Optional<List<CcMmReplPack>> findCcMmReplnPkConsData(@Param("planId")Long planId, @Param("fineline") Integer fineline, @Param("customerChoice") String customerChoice);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "delete from dbo.rc_cc_mm_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and fineline_nbr in (:finelineNbrs)", nativeQuery = true)
	void deleteByPlanIdFinelineIdChannelId(@Param("planId") Long planId, @Param("channelId") Integer channelId, @Param("finelineNbrs") Set<Integer> finelineNbrs);
}
