package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.DeptAdminRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptAdminRuleRepository extends JpaRepository<DeptAdminRule, Integer> {
    DeptAdminRule findByDeptNbr(Integer deptNbr);
}
