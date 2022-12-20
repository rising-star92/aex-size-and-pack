package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.StyleReplPackId;
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
public interface StyleReplnPkConsRepository extends JpaRepository <StyleReplPack, StyleReplPackId> {

	@Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "select * from dbo.rc_style_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and rpt_lvl_3_nbr = :lvl3Nbr \n" + 
			"and rpt_lvl_4_nbr = :lvl4Nbr and fineline_nbr=:fineline and style_nbr=:style ", nativeQuery = true)
	List<StyleReplPack> getStyleReplnConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr, 
			@Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline, @Param("style") String style);

	@Query(value="select srp from StyleReplPack srp join CcReplPack  crp on " +
			"crp.ccReplPackId.styleReplPackId.styleNbr = srp.styleReplPackId.styleNbr " +
			"And crp.ccReplPackId.styleReplPackId.finelineReplPackId.finelineNbr = srp.styleReplPackId.finelineReplPackId.finelineNbr " +
			"where srp.styleReplPackId.finelineReplPackId.finelineNbr=:finelineNbr " +
			"and srp.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId=:planId " +
			"and crp.ccReplPackId.customerChoice=:ccId" +
			" and srp.styleReplPackId.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId=1")
    Optional<StyleReplPack> findByPlanIdAndCCId(@Param("planId")  Long planId, @Param("finelineNbr")  Integer finelineNbr,@Param("ccId")  String ccId);
	void deleteByStyleReplPackId_FinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_planIdAndStyleReplPackId_FinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_repTLvl3AndStyleReplPackId_FinelineReplPackId_SubCatgReplPackId_repTLvl4AndStyleReplPackId_FinelineReplPackId_finelineNbrAndStyleReplPackId_styleNbr(Long planId, Integer lvl3Nbr, Integer lvl4Nbr,Integer finelineNbr,String styleNbr);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "delete from dbo.rc_style_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and fineline_nbr in (:finelineNbrs)", nativeQuery = true)
	void deleteByPlanIdFinelineIdChannelId(@Param("planId") Long planId, @Param("channelId") Integer channelId, @Param("finelineNbrs") Set<Integer> finelineNbrs);

}
