package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.DeptAdminRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptAdminRuleRepository extends JpaRepository<DeptAdminRule, Integer> {
    DeptAdminRule findByDeptNbr(Integer deptNbr);
    DeptAdminRule findByReplItemPieceRule(Integer replItemPieceRule);
    DeptAdminRule findByMinReplItemUnits(Integer minReplItemUnits);
    DeptAdminRule findByDeptNbrAndReplItemPieceRule(Integer deptNbr, Integer replItemPieceRule);
    DeptAdminRule findByDeptNbrAndMinReplItemUnits(Integer deptNbr, Integer minReplItemUnits);
    DeptAdminRule findByReplItemPieceRuleAndMinReplItemUnits(Integer replItemPieceRule, Integer minReplItemUnits);
    DeptAdminRule findByDeptNbrAndReplItemPieceRuleAndMinReplItemUnits(Integer deptNbr, Integer replItemPieceRule, Integer minReplItemUnits);
}
