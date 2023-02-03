package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.dto.mapper.DeptAdminRuleMapper;
import com.walmart.aex.sp.entity.DeptAdminRule;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.DeptAdminRuleRepository;
import com.walmart.aex.sp.service.DeptAdminRuleService;
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
public class DeptAdminRuleServiceImpl implements DeptAdminRuleService {

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
    public void deleteDeptAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests) {
        try {
            if(!CollectionUtils.isEmpty(deptAdminRuleRequests)) {
                List<Integer> deletionList = new ArrayList<>();
                List<Integer> recordDoesNotExist = new ArrayList<>();
                Set<Integer> deptNbrs = deptAdminRuleRequests.stream().map(DeptAdminRuleRequest::getDeptNbr).collect(Collectors.toSet());
                for (Integer deptNbr: deptNbrs) {
                    Optional<DeptAdminRule> existing = deptAdminRuleRepository.findById(deptNbr);
                    if(existing.isPresent()) {
                        deletionList.add(deptNbr);
                    } else {
                        recordDoesNotExist.add(deptNbr);
                    }
                }
                deptAdminRuleRepository.deleteAllById(deletionList);
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
}
