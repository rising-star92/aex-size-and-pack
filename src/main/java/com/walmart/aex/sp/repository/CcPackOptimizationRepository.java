package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CcPackOptimizationRepository extends JpaRepository<CcPackOptimization, CcPackOptimizationID> {
}
