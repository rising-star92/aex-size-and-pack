package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.StylePlan;
import com.walmart.aex.sp.entity.StylePlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StylePlanRepository extends JpaRepository<StylePlan, StylePlanId> {

    void deleteByStylePlanId_FinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndStylePlanId_FinelinePlanId_SubCatPlanId_MerchCatPlanId_lvl3NbrAndStylePlanId_FinelinePlanId_SubCatPlanId_lvl4NbrAndStylePlanId_FinelinePlanId_FinelineNbrAndStylePlanId_styleNbr(Long planId,Integer lvl3Nbr, Integer lvl4Nbr ,Integer finelineNbr, String styleNbr);
}
