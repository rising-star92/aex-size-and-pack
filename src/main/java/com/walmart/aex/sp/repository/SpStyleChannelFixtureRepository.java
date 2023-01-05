package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.SpStyleChannelFixture;
import com.walmart.aex.sp.entity.SpStyleChannelFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Set;

public interface SpStyleChannelFixtureRepository extends JpaRepository<SpStyleChannelFixture, SpStyleChannelFixtureId> {
    void deleteBySpStyleChannelFixtureId_SpFineLineChannelFixtureId_planIdAndSpStyleChannelFixtureId_SpFineLineChannelFixtureId_lvl3NbrAndSpStyleChannelFixtureId_SpFineLineChannelFixtureId_lvl4NbrAndSpStyleChannelFixtureId_SpFineLineChannelFixtureId_fineLineNbrAndSpStyleChannelFixtureId_styleNbr(Long planId, Integer lvl3Nbr, Integer lvl4Nbr,Integer finelineNbr, String styleNbr);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from dbo.sp_style_chan_fixtr where plan_id = :planId and channel_id = :channelId and fineline_nbr in (:finelineNbrs)", nativeQuery = true)
    void deleteByPlanIdFinelineIdChannelId(@Param("planId") Long planId, @Param("channelId") Integer channelId, @Param("finelineNbrs") Set<Integer> finelineNbrs);
}
