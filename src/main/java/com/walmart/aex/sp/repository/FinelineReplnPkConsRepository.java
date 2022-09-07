package com.walmart.aex.sp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.walmart.aex.sp.entity.FinelineReplPack;
import com.walmart.aex.sp.entity.FinelineReplPackId;

@Repository
public interface FinelineReplnPkConsRepository extends JpaRepository <FinelineReplPack, FinelineReplPackId>{

     @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "select * from dbo.rc_fl_replpk_fixtr_cons where plan_id = :planId and channel_id = :channelId and rpt_lvl_3_nbr = :lvl3Nbr \n" +
            "and rpt_lvl_4_nbr = :lvl4Nbr and fineline_nbr=:fineline ", nativeQuery = true)
    List<FinelineReplPack> getFinelineReplnConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr,
                                                    @Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline);
    @Query(value="select frp from FinelineReplPack frp " +
            "where frp.finelineReplPackId.finelineNbr=:finelineNbr and frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId=:planId and frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.channelId=1")
    Optional<FinelineReplPack> findByPlanIdAndFinelineNbr(@Param("planId") Long planId,@Param("finelineNbr") Integer finelineNbr);
}
