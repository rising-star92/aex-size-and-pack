package com.walmart.aex.sp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.walmart.aex.sp.entity.SubCatgReplPack;
import com.walmart.aex.sp.entity.SubCatgReplPackId;

@Repository
public interface SubCatgReplnPkConsRepository extends JpaRepository<SubCatgReplPack, SubCatgReplPackId> {

	@Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "select * from dbo.rc_subcatg_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and rpt_lvl_3_nbr = :lvl3Nbr \n" +
            "and rpt_lvl_4_nbr = :lvl4Nbr  ", nativeQuery = true)
    List<SubCatgReplPack> getSubCatgReplnConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, 
    		@Param("lvl3Nbr") Integer lvl3Nbr, @Param("lvl4Nbr") Integer lvl4Nbr);
    @Query(value="select crp from SubCatgReplPack crp join FinelineReplPack  frp on " +
            "crp.subCatgReplPackId.merchCatgReplPackId.repTLvl3 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "And crp.subCatgReplPackId.merchCatgReplPackId.planId = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "And crp.subCatgReplPackId.merchCatgReplPackId.repTLvl0 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "And crp.subCatgReplPackId.merchCatgReplPackId.repTLvl1 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "And crp.subCatgReplPackId.merchCatgReplPackId.repTLvl2 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "where frp.finelineReplPackId.finelineNbr=:finelineNbr and crp.subCatgReplPackId.merchCatgReplPackId.planId=:planId and crp.subCatgReplPackId.merchCatgReplPackId.channelId=1")
    Optional<SubCatgReplPack> findByPlanIdAndFinelineNbr(@Param("planId")  Long planId, @Param("finelineNbr")  Integer finelineNbr);
}
