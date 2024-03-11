package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.DeptAdminRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface DeptAdminRuleRepository extends JpaRepository<DeptAdminRule, Integer> {
    @Query(value = "select dept_nbr, repl_item_piece_rule , min_repl_item_units from dbo.plan_admin_rules where dept_nbr = :deptNbr" +
            " and plan_id = :planId union all select dept_nbr , repl_item_piece_rule, min_repl_item_units from dbo.dept_admin_rules where dept_nbr = :deptNbr " +
            "and not exists (select 1 from dbo.plan_admin_rules where dept_nbr = :deptNbr and plan_id = :planId) ", nativeQuery = true)
    DeptAdminRule getReplnRuleCons(@Param("planId")Long planId, @Param("deptNbr") Integer deptNbr);
}