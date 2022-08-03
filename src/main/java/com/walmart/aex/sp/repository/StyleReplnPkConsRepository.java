package com.walmart.aex.sp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.walmart.aex.sp.entity.StyleReplenishmentPack;
import com.walmart.aex.sp.entity.StyleReplenishmentPackId;

@Repository
public interface StyleReplnPkConsRepository extends JpaRepository <StyleReplenishmentPack, StyleReplenishmentPackId> {

	@Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "select * from dbo.style_replpk_cons where plan_id = :planId and channel_id = :channelId and rpt_lvl_3_nbr = :lvl3Nbr \n" + 
			"and rpt_lvl_4_nbr = :lvl4Nbr and fineline_nbr=:fineline and style_nbr=:style", nativeQuery = true)
	List<StyleReplenishmentPack> getCcReplnConsData(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr, 
			@Param("lvl4Nbr") Integer lvl4Nbr, @Param("fineline") Integer fineline, @Param("style") String style);
}
