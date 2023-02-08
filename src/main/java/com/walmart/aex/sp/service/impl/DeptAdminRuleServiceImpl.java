package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.dto.deptadminrule.ReplItemResponse;
import com.walmart.aex.sp.dto.mapper.DeptAdminRuleMapper;
import com.walmart.aex.sp.entity.DeptAdminRule;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import com.walmart.aex.sp.repository.DeptAdminRuleRepository;
import com.walmart.aex.sp.service.DeptAdminRuleService;
import com.walmart.aex.sp.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.walmart.aex.sp.util.SizeAndPackConstants.DEFAULT_MIN_REPL_ITEM_UNITS;
import static com.walmart.aex.sp.util.SizeAndPackConstants.DEFAULT_REPL_ITEM_PIECE_RULE;

@Service
@Slf4j
@Transactional
public class DeptAdminRuleServiceImpl implements DeptAdminRuleService {

    @ManagedConfiguration
    private BuyQtyProperties buyQtyProperties;

    private final DeptAdminRuleRepository deptAdminRuleRepository;

    public DeptAdminRuleServiceImpl(DeptAdminRuleRepository deptAdminRuleRepository) {
        this.deptAdminRuleRepository = deptAdminRuleRepository;
    }

    @Override
    public List<DeptAdminRuleResponse> getDeptAdminRules(List<Integer> deptNumbers) {
        List<DeptAdminRule> deptAdminRuleList;
        if(CollectionUtils.isEmpty(deptNumbers)) {
            deptAdminRuleList = deptAdminRuleRepository.findAll();
        } else {
            deptAdminRuleList = deptAdminRuleRepository.findAllById(deptNumbers);
        }
        return DeptAdminRuleMapper.deptAdminRuleMapper.depAdminRulesToDeptAdminRuleResponses(deptAdminRuleList);
    }

    @Override
    public void addAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests) {
        if(!CollectionUtils.isEmpty(deptAdminRuleRequests)) {
            List<DeptAdminRule> deptAdminRules = DeptAdminRuleMapper.deptAdminRuleMapper.deptAdminRuleRequestToDeptAdminRules(deptAdminRuleRequests);
            deptAdminRuleRepository.saveAll(deptAdminRules);
        }
    }

    @Override
    public void updateAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests) {
        List<DeptAdminRule> updatedRecords = new ArrayList<>();
        if(!CollectionUtils.isEmpty(deptAdminRuleRequests)) {
            List<DeptAdminRule> deptAdminRules = DeptAdminRuleMapper.deptAdminRuleMapper.deptAdminRuleRequestToDeptAdminRules(deptAdminRuleRequests);
            for (DeptAdminRule deptAdminRule : deptAdminRules) {
                Optional<DeptAdminRule> existing = deptAdminRuleRepository.findById(deptAdminRule.getDeptNbr());
                if(existing.isPresent()) {
                    updatedRecords.add(deptAdminRule);
                }
            }
            if(!CollectionUtils.isEmpty(updatedRecords)) {
                deptAdminRuleRepository.saveAll(updatedRecords);
            }
        }
    }

    @Override
    public void deleteDeptAdminRules(List<Integer> deptNbrs) {
        try {
            if(!CollectionUtils.isEmpty(deptNbrs)) {
                Set<Integer> uniqueDeptNbrs = new HashSet<>(deptNbrs);
                List<Integer> deletionList = new ArrayList<>();
                List<Integer> recordDoesNotExist = new ArrayList<>();
                for (Integer deptNbr: uniqueDeptNbrs) {
                    Optional<DeptAdminRule> existing = deptAdminRuleRepository.findById(deptNbr);
                    if(existing.isPresent()) {
                        deletionList.add(deptNbr);
                    } else {
                        recordDoesNotExist.add(deptNbr);
                    }
                }
                if(!CollectionUtils.isEmpty(deletionList)) {
                    deptAdminRuleRepository.deleteAllById(deletionList);
                }
                if(!CollectionUtils.isEmpty(recordDoesNotExist)) {
                    log.error("Failed to delete DeptAdminRuleRequests in db. Depts: {}", recordDoesNotExist);
                }
            }
        }
        catch (Exception ex) {
            log.error("Failed to delete DeptAdminRuleRequests in db. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to delete DeptAdminRuleRequests in db.");
        }
    }

    @Override
    public ReplItemResponse getReplItemRule(Long planId, Integer lvl1Nbr) {
        ReplItemResponse response = new ReplItemResponse();
        String plans = buyQtyProperties.getS3PlanIds();
        int currentPlan = Math.toIntExact(planId);
        List<Integer> s3Plans2024 = CommonUtil.getNumbersFromString(plans);
        if(s3Plans2024.contains(currentPlan)) {
            response.setReplItemPieceRule(buyQtyProperties.getInitialThreshold());
            response.setMinReplItemUnits(buyQtyProperties.getReplenishmentThreshold());
        } else {
            List<DeptAdminRuleResponse> deptAdminRules = getDeptAdminRules(List.of(lvl1Nbr));
            if(CollectionUtils.isEmpty(deptAdminRules)) {
                response.setReplItemPieceRule(DEFAULT_REPL_ITEM_PIECE_RULE);
                response.setMinReplItemUnits(DEFAULT_MIN_REPL_ITEM_UNITS);
            } else {
                response.setReplItemPieceRule(deptAdminRules.iterator().next().getReplItemPieceRule());
                response.setMinReplItemUnits(deptAdminRules.iterator().next().getMinReplItemUnits());
            }
        }
        return response;
    }

    @Override
    public Integer getInitialThreshold(Long planId, Integer lvl1Nbr) {
        String plans = buyQtyProperties.getS3PlanIds();
        int currentPlan = Math.toIntExact(planId);
        List<Integer> s3Plans2024 = CommonUtil.getNumbersFromString(plans);
        if(s3Plans2024.contains(currentPlan)) {
            return buyQtyProperties.getInitialThreshold();
        } else {
            int deptNumber = lvl1Nbr;
            List<DeptAdminRuleResponse> deptAdminRules = getDeptAdminRules(List.of(deptNumber));
            if(CollectionUtils.isEmpty(deptAdminRules)) {
                return DEFAULT_REPL_ITEM_PIECE_RULE;
            } else {
                return deptAdminRules.iterator().next().getReplItemPieceRule();
            }
        }
    }

    @Override
    public Integer getReplenishmentThreshold(Long planId, Integer lvl1Nbr) {
        int currentPlan = Math.toIntExact(planId);
        String plans = buyQtyProperties.getS3PlanIds();
        List<Integer> s3Plans2024 = CommonUtil.getNumbersFromString(plans);
        if(s3Plans2024.contains(currentPlan)) {
            return buyQtyProperties.getReplenishmentThreshold();
        } else {
            List<DeptAdminRuleResponse> deptAdminRules = getDeptAdminRules(List.of(lvl1Nbr));
            if(CollectionUtils.isEmpty(deptAdminRules)) {
                return DEFAULT_MIN_REPL_ITEM_UNITS;
            } else {
                return deptAdminRules.iterator().next().getMinReplItemUnits();
            }
        }
    }
}
