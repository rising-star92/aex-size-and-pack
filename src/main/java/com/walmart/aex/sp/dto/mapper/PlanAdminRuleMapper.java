package com.walmart.aex.sp.dto.mapper;

import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;
import com.walmart.aex.sp.entity.PlanAdminRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.walmart.aex.security.service.UserDetailsService.getAuthenticatedUserName;

@Mapper
public interface PlanAdminRuleMapper {
    PlanAdminRuleMapper mapper = Mappers.getMapper(PlanAdminRuleMapper.class);
    List<PlanAdminRule> mapRequestToEntity(List<PlanAdminRuleRequest> requests);

    Set<PlanAdminRuleResponse> mapEntityToResponse(List<PlanAdminRule> entity);
}