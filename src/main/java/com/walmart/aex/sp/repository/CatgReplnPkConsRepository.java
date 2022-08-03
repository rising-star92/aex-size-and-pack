package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.FinelineReplenishmentPack;
import com.walmart.aex.sp.entity.MerchCatgReplenishmentPack;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CatgReplnPkConsRepository extends JpaRepository<MerchCatgReplenishmentPack, MerchantPackOptimizationID> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "select * from dbo.merchcatg_replpk_cons where plan_id = :planId and channel_id = :channelId and rpt_lvl_3_nbr = :lvl3Nbr"
            , nativeQuery = true)
    List<MerchCatgReplenishmentPack> getCatgReplnConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr);


}
