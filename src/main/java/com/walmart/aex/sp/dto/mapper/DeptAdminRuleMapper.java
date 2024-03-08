package com.walmart.aex.sp.dto.mapper;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;
import com.walmart.aex.sp.entity.DeptAdminRule;
import com.walmart.aex.sp.entity.PlanAdminRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper
public interface DeptAdminRuleMapper {
    DeptAdminRuleMapper deptAdminRuleMapper = Mappers.getMapper(DeptAdminRuleMapper.class);
    @Mapping(source = "planAdminRuleResponses", target = "planAdminRuleResponses")
    @Mapping(source = "deptAdminRule.deptNbr", target = "deptNbr")
    @Mapping(source = "deptAdminRule.replItemPieceRule", target = "replItemPieceRule")
    @Mapping(source = "deptAdminRule.minReplItemUnits", target = "minReplItemUnits")
    DeptAdminRuleResponse depAdminRulesToDeptAdminRuleResponses(DeptAdminRule deptAdminRule, Set<PlanAdminRuleResponse> planAdminRuleResponses);

    Set<PlanAdminRuleResponse> mapPlanAdminRuleEntityListToResponseSet(Set<PlanAdminRule> planAdminRule);

    List<DeptAdminRule> deptAdminRuleRequestToDeptAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests);

}
