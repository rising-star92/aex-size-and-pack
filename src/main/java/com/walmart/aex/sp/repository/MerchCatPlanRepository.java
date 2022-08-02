package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchCatPlan;
import com.walmart.aex.sp.entity.MerchCatPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchCatPlanRepository extends JpaRepository<MerchCatPlan, MerchCatPlanId> {
    List<MerchCatPlan> findMerchCatPlanByMerchCatPlanId_planIdAndMerchCatPlanId_lvl0NbrAndMerchCatPlanId_lvl1NbrAndMerchCatPlanId_lvl2NbrAndMerchCatPlanId_lvl3Nbr(Long planId, Integer lvl0Nbr,Integer lvl1Nbr,  Integer lvl2Nbr,Integer lvl3Nbr);
}
