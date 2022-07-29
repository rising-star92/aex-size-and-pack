package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchCatPlan;
import com.walmart.aex.sp.entity.MerchCatPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchCatPlanRepository extends JpaRepository<MerchCatPlan, MerchCatPlanId> {
}
