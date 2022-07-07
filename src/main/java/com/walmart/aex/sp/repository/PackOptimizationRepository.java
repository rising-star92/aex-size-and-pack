package com.walmart.aex.sp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;

public interface PackOptimizationRepository
extends JpaRepository<MerchantPackOptimization, MerchantPackOptimizationID> {

	List<MerchantPackOptimization> findByMerchantPackOptimizationIDPlanIdAndChannelTextChannelId(Long planid, Integer channelid);



}
