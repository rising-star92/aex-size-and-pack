package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.CustChoicePlan;
import com.walmart.aex.sp.entity.CustChoicePlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerChoiceRepository extends JpaRepository<CustChoicePlan, CustChoicePlanId> {
}
