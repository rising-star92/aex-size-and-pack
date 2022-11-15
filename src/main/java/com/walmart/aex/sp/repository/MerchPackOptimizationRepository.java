package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchPackOptimizationRepository extends JpaRepository<MerchantPackOptimization, MerchantPackOptimizationID> {
    List<MerchantPackOptimization> findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl3AndChannelText_channelId(Long planId, Integer lvl3Nbr, Integer channel);
}
