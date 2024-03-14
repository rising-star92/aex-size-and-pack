package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;
import com.walmart.aex.sp.dto.mapper.PlanAdminRuleMapper;
import com.walmart.aex.sp.entity.PlanAdminRule;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.PlanAdminRulesRespository;
import com.walmart.aex.sp.service.PlanAdminRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.security.service.UserDetailsService.getAuthenticatedUserName;

@Service
@Slf4j
@Transactional
public class PlanAdminRuleServiceImpl implements PlanAdminRuleService {

    private final PlanAdminRulesRespository planAdminRulesRespository;

    public PlanAdminRuleServiceImpl(PlanAdminRulesRespository planAdminRulesRespository) {
        this.planAdminRulesRespository = planAdminRulesRespository;
    }

    /** This methods returns all the existing Plan Admin rule
     * @return List<PlanAdminRuleResponse>
     */
    @Override
    public Set<PlanAdminRuleResponse> getPlanAdminRules(List<Long> planIds) {
        List<PlanAdminRule> planAdminRules;
        try {
            if (!CollectionUtils.isEmpty(planIds)){
                planAdminRules = planAdminRulesRespository.findAllById(planIds);
            }else{
                planAdminRules = planAdminRulesRespository.findAll();
            }
            return PlanAdminRuleMapper.mapper.mapEntityToResponse(planAdminRules);
        } catch (Exception ex) {
            log.error("Error while getting list of plan admin rule. Exception: {}", ex.getMessage());
            throw new CustomException("Exception occurred while retrieving plan admin rule");
        }
    }

    /** This methods adds all the existing Plan Admin rule
     */
    @Override
    public void addPlanAdminRules(List<PlanAdminRuleRequest> planAdminRuleRequests) {
        try {
            if (!CollectionUtils.isEmpty(planAdminRuleRequests)) {
                Date createDate = new Date();
                String userId = getAuthenticatedUserName();
                planAdminRuleRequests.forEach(planAdminRuleRequest -> {
                    planAdminRuleRequest.setCreateTs(createDate);
                    planAdminRuleRequest.setCreateUserId(Objects.nonNull(planAdminRuleRequest.getCreateUserId()) ? planAdminRuleRequest.getCreateUserId() : userId);
                });
                List<PlanAdminRule> planAdminRules = PlanAdminRuleMapper.mapper.mapRequestToEntity(planAdminRuleRequests);
                planAdminRulesRespository.saveAll(planAdminRules);
            }
        } catch (Exception ex) {
            log.error("Failed to add new plan admin rule. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to add plan admin rule");
        }
    }

    /** This method updates all the existing Plan Admin rule
     */
    @Override
    public void updatePlanAdminRules(List<PlanAdminRuleRequest> plaAdminRuleRequests) {
        List<PlanAdminRule> updatedRecords = new ArrayList<>();
        Date updateDate = new Date();
        String userId = getAuthenticatedUserName();
        if(!CollectionUtils.isEmpty(plaAdminRuleRequests)) {
            Set<Long> planIds = plaAdminRuleRequests.stream().map(PlanAdminRuleRequest::getPlanId).collect(Collectors.toSet());
            List<PlanAdminRule> existingPlanAdminRules = planAdminRulesRespository.findAllById(planIds);
            for (PlanAdminRule planAdminRule : existingPlanAdminRules) {
                PlanAdminRuleRequest request = plaAdminRuleRequests.stream().filter(planAdminRule1 -> planAdminRule1.getPlanId().equals(planAdminRule.getPlanId())).findFirst().orElse(null);
                if(Objects.nonNull(request)) {
                    planAdminRule.setLastModifiedTs(updateDate);
                    planAdminRule.setLastModifiedUserId(Objects.nonNull(request.getLastModifiedUserId()) ? request.getLastModifiedUserId() : userId);
                    planAdminRule.setReplItemPieceRule(request.getReplItemPieceRule());
                    planAdminRule.setMinReplItemUnits(request.getMinReplItemUnits());
                    updatedRecords.add(planAdminRule);
                }
            }
            if(!CollectionUtils.isEmpty(updatedRecords)) {
                planAdminRulesRespository.saveAll(updatedRecords);
            }
        }
    }

    /** This method deletes all the existing Plan Admin rule
     */
    @Override
    public void deletePlanAdminRule(List<Long> planIds) {
        try {
            if (!CollectionUtils.isEmpty(planIds)) {
                planAdminRulesRespository.deleteAllById(planIds);
            }
        } catch (Exception ex) {
            log.error("Failed to delete plan admin rule. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to delete plan admin rule.");
        }
    }
}
