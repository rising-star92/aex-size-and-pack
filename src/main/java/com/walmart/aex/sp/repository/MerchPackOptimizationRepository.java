package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MerchPackOptimizationRepository extends JpaRepository<MerchantPackOptimization, MerchantPackOptimizationID> {
    Optional<List<MerchantPackOptimization>> findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl3(Long planId, Integer lvl3Nbr);
}
