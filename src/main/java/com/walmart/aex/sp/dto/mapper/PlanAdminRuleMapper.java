package com.walmart.aex.sp.dto.mapper;

import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;
import com.walmart.aex.sp.entity.PlanAdminRule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlanAdminRuleMapper {
    PlanAdminRuleMapper mapper = Mappers.getMapper(PlanAdminRuleMapper.class);
    List<PlanAdminRule> mapRequestToEntity(List<PlanAdminRuleRequest> requests);

    List<PlanAdminRuleResponse> mapEntityToResponse(List<PlanAdminRule> entity);

}
