package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.CcReplPack;
import com.walmart.aex.sp.entity.CcReplPackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CcReplnPkConsRepository extends JpaRepository <CcReplPack, CcReplPackId> {

	@Query(value = "select * from dbo.rc_cc_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and rpt_lvl_3_nbr = :lvl3Nbr \n" + 
			"and rpt_lvl_4_nbr = :lvl4Nbr and fineline_nbr=:fineline and style_nbr=:style and customer_choice = :customerChoice ", nativeQuery = true)
	List<CcReplPack> getCcReplnConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr, 
			@Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline, @Param("style") String style, 
			@Param("customerChoice") String customerChoice);

	@Query(value="select crp from CcReplPack crp " +
			"where crp.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr=:finelineNbr " +
			"and crp.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId=:planId " +
			"and crp.ccReplPackId.customerChoice=:ccId " +
			"and crp.ccReplPackId.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId=1")
	Optional<List<CcReplPack>> findByPlanIdAndCCId(@Param("planId")  Long planId, @Param("finelineNbr")  Integer finelineNbr,@Param("ccId")  String ccId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "delete from dbo.rc_cc_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and fineline_nbr in (:finelineNbrs)", nativeQuery = true)
	void deleteByPlanIdFinelineIdChannelId(@Param("planId") Long planId, @Param("channelId") Integer channelId, @Param("finelineNbrs") Set<Integer> finelineNbrs);
}
