package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.FinelinePlan;
import com.walmart.aex.sp.entity.FinelinePlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FinelinePlanRepository extends JpaRepository<FinelinePlan, FinelinePlanId> {
   Optional<FinelinePlan> findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(Long planId, Integer finelineNbr, Integer channel);

   void deleteByfinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbr(Long planId, Integer finelineNbr);

}
