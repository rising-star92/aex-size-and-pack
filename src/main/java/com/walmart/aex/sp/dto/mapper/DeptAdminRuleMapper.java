package com.walmart.aex.sp.dto.mapper;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.entity.DeptAdminRule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeptAdminRuleMapper {
    DeptAdminRuleMapper deptAdminRuleMapper = Mappers.getMapper(DeptAdminRuleMapper.class);

    List<DeptAdminRuleResponse> depAdminRulesToDeptAdminRuleResponses(List<DeptAdminRule> deptAdminRules);

    List<DeptAdminRule> deptAdminRuleRequestToDeptAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests);

}
