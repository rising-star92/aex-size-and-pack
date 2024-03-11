package com.walmart.aex.sp.dto.mapper;

import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;
import com.walmart.aex.sp.entity.PlanAdminRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

import static com.walmart.aex.security.service.UserDetailsService.getAuthenticatedUserName;

@Mapper
public interface PlanAdminRuleMapper {
    PlanAdminRuleMapper mapper = Mappers.getMapper(PlanAdminRuleMapper.class);
    @Mapping(target = "createTs", expression = "java(getCurrentDate())")
    @Mapping(target = "createUserId", expression = "java(getUserId())")
    PlanAdminRule mapRequestToEntity(PlanAdminRuleRequest request);
    List<PlanAdminRule> mapRequestListToEntityList(List<PlanAdminRuleRequest> requests);

    List<PlanAdminRuleResponse> mapEntityToResponse(List<PlanAdminRule> entity);

    default Date getCurrentDate() {
        return new Date();
    }

    default String getUserId() {
        return getAuthenticatedUserName();
    }

}
