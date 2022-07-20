package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpFineLineChannelFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineLineBuyRepo extends JpaRepository <SpFineLineChannelFixture, SpFineLineChannelFixtureId>
    {
        List<SpFineLineChannelFixture> findBySpFineLineChannelFixtureIdPlanIdAndSpFineLineChannelFixtureIdChannelId(long planId, Integer channelId);
    }

