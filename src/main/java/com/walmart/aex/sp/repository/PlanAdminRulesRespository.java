package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.PlanAdminRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanAdminRulesRespository extends JpaRepository<PlanAdminRule, Long> {
}
