package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.FinelineReplenishmentPack;
import com.walmart.aex.sp.entity.SubCatgReplenishmentPack;
import com.walmart.aex.sp.entity.SubCatgReplenishmentPackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubCatgReplnPkConsRepository extends JpaRepository<SubCatgReplenishmentPack, SubCatgReplenishmentPackId> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "select * from dbo.subcatg_replpk_cons where plan_id = :planId and channel_id = :channelId and rpt_lvl_3_nbr = :lvl3Nbr \n" +
            "and rpt_lvl_4_nbr = :lvl4Nbr", nativeQuery = true)
    List<SubCatgReplenishmentPack> getSubCatgReplnConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr, @Param("lvl4Nbr") Integer lvl4Nbr);

}
