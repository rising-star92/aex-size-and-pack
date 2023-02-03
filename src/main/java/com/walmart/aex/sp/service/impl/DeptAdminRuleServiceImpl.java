package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.dto.mapper.DeptAdminRuleMapper;
import com.walmart.aex.sp.entity.DeptAdminRule;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.DeptAdminRuleRepository;
import com.walmart.aex.sp.service.DeptAdminRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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
    public List<DeptAdminRuleResponse> getDeptAdminRules(DeptAdminRuleRequest deptAdminRuleRequest) {
        List<DeptAdminRule> deptAdminRuleList;
        if(ObjectUtils.isEmpty(deptAdminRuleRequest)) {
            deptAdminRuleList = deptAdminRuleRepository.findAll();
        } else {
            DeptAdminRule deptAdminRule = getDeptAdminRule(deptAdminRuleRequest);
            if (ObjectUtils.isEmpty(deptAdminRule)) {
                return new ArrayList<>();
            }
            deptAdminRuleList = List.of(deptAdminRule);
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
    public void deleteDeptAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests) {
        try {
            if(!CollectionUtils.isEmpty(deptAdminRuleRequests)) {
                Set<Integer> deptNbrs = deptAdminRuleRequests.stream().map(DeptAdminRuleRequest::getDeptNbr).collect(Collectors.toSet());
                deptAdminRuleRepository.deleteAllById(deptNbrs);
            }
        }
        catch (Exception ex) {
            log.error("Failed to delete DeptAdminRuleRequests in db. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to delete DeptAdminRuleRequests in db.");
        }
    }

    private DeptAdminRule getDeptAdminRule(DeptAdminRuleRequest deptAdminRuleRequest){
        if (deptAdminRuleRequest.getDeptNbr() != null && deptAdminRuleRequest.getReplItemPieceRule() != null && deptAdminRuleRequest.getMinReplItemUnits() != null) {
            return deptAdminRuleRepository.findByDeptNbrAndReplItemPieceRuleAndMinReplItemUnits(deptAdminRuleRequest.getDeptNbr(), deptAdminRuleRequest.getReplItemPieceRule(), deptAdminRuleRequest.getMinReplItemUnits());
        }
        if (deptAdminRuleRequest.getDeptNbr() != null && deptAdminRuleRequest.getReplItemPieceRule() != null) {
            return deptAdminRuleRepository.findByDeptNbrAndReplItemPieceRule(deptAdminRuleRequest.getDeptNbr(), deptAdminRuleRequest.getReplItemPieceRule());
        }
        if (deptAdminRuleRequest.getDeptNbr() != null && deptAdminRuleRequest.getMinReplItemUnits() != null) {
            return deptAdminRuleRepository.findByDeptNbrAndMinReplItemUnits(deptAdminRuleRequest.getDeptNbr(), deptAdminRuleRequest.getMinReplItemUnits());
        }
        if (deptAdminRuleRequest.getReplItemPieceRule() != null && deptAdminRuleRequest.getMinReplItemUnits() != null) {
            return deptAdminRuleRepository.findByReplItemPieceRuleAndMinReplItemUnits(deptAdminRuleRequest.getReplItemPieceRule(), deptAdminRuleRequest.getMinReplItemUnits());
        }
        if (deptAdminRuleRequest.getDeptNbr() != null && deptAdminRuleRequest.getReplItemPieceRule() == null && deptAdminRuleRequest.getMinReplItemUnits() == null) {
            return deptAdminRuleRepository.findByDeptNbr(deptAdminRuleRequest.getDeptNbr());
        }
        if (deptAdminRuleRequest.getReplItemPieceRule() != null && deptAdminRuleRequest.getDeptNbr() == null && deptAdminRuleRequest.getMinReplItemUnits() == null) {
            return deptAdminRuleRepository.findByReplItemPieceRule(deptAdminRuleRequest.getReplItemPieceRule());
        }
        return deptAdminRuleRepository.findByMinReplItemUnits(deptAdminRuleRequest.getMinReplItemUnits());
    }
}
