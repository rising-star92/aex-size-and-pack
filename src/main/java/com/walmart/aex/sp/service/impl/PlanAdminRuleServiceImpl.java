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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<PlanAdminRuleResponse> getPlanAdminRules(List<Long> planIds) {
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
        if(!CollectionUtils.isEmpty(plaAdminRuleRequests)) {
            List<PlanAdminRule> planAdminRules = PlanAdminRuleMapper.mapper.mapRequestToEntity(plaAdminRuleRequests);
            Set<Long> planIds = plaAdminRuleRequests.stream().map(PlanAdminRuleRequest::getPlanId).collect(Collectors.toSet());
            List<PlanAdminRule> existingPlanAdminRules = planAdminRulesRespository.findAllById(planIds);
            for (PlanAdminRule planAdminRule : planAdminRules) {
                Optional<PlanAdminRule> existing = existingPlanAdminRules.stream().filter(planAdminRule1 -> planAdminRule1.getPlanId().equals(planAdminRule.getPlanId())).findAny();
                if(existing.isPresent()) {
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
