package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MerchPackOptimizationRepository extends JpaRepository<MerchantPackOptimization, MerchantPackOptimizationID> {
    List<MerchantPackOptimization> findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl3AndChannelText_channelId(Long planId, Integer lvl3Nbr, Integer channel);
    List<MerchantPackOptimization> findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl0AndMerchantPackOptimizationID_repTLvl1AndMerchantPackOptimizationID_repTLvl2AndMerchantPackOptimizationID_repTLvl3(Long planId,Integer lvl0Nbr,Integer lvl1Nbr,Integer lvl2Nbr, Integer lvl3Nbr);
}
