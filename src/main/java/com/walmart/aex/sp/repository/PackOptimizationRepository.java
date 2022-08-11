package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PackOptimizationRepository
extends JpaRepository<MerchantPackOptimization, MerchantPackOptimizationID> {

	List<MerchantPackOptimization> findByMerchantPackOptimizationIDPlanIdAndChannelTextChannelId(Long planid, Integer channelid);
}