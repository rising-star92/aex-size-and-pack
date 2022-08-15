package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MerchCatgReplPackRepository extends JpaRepository<MerchCatgReplPack, MerchCatgReplPackId> {
    Optional<List<MerchCatgReplPack>> findMerchCatgReplPackByMerchCatgReplPackId_planIdAndMerchCatgReplPackId_channelId(Long planId, Integer channelIdFromName);

    @Query(value="select crp from MerchCatgReplPack crp join FinelineReplPack  frp on " +
            "crp.merchCatgReplPackId.repTLvl3 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl3 " +
            "And crp.merchCatgReplPackId.planId = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.planId " +
            "And crp.merchCatgReplPackId.repTLvl0 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl0 " +
            "And crp.merchCatgReplPackId.repTLvl1 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl1 " +
            "And crp.merchCatgReplPackId.repTLvl2 = frp.finelineReplPackId.subCatgReplPackId.merchCatgReplPackId.repTLvl2 " +
            "where frp.finelineReplPackId.finelineNbr=:finelineNbr and crp.merchCatgReplPackId.planId=:planId " +
            "and crp.merchCatgReplPackId.channelId=1")
    Optional<MerchCatgReplPack> findByPlanIdAndFinelineNbr(@Param("planId") Long planId,@Param("finelineNbr") Integer finelineNbr);

}
