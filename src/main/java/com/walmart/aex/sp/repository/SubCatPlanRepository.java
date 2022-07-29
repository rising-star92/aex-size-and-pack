package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchCatPlan;
import com.walmart.aex.sp.entity.MerchCatPlanId;
import com.walmart.aex.sp.entity.SubCatPlan;
import com.walmart.aex.sp.entity.SubCatPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCatPlanRepository extends JpaRepository<SubCatPlan, SubCatPlanId> {
}
