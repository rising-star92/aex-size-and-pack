package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.FinelinePlan;
import com.walmart.aex.sp.entity.FinelinePlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinelinePlanRepository extends JpaRepository<FinelinePlan, FinelinePlanId> {
}
