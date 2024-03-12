package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;
import com.walmart.aex.sp.entity.PlanAdminRule;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.PlanAdminRulesRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanAdminRuleServiceImplTest {
    @InjectMocks
    private PlanAdminRuleServiceImpl planAdminRuleService;

    @Mock
    private PlanAdminRulesRespository planAdminRulesRespository;

    private List<PlanAdminRule> dbResponse;

    private Set<PlanAdminRuleResponse> actual;
    private PlanAdminRuleRequest planAdminRuleRequest;
    @Captor
    private ArgumentCaptor<List<PlanAdminRule>> planAdminRuleCaptor;
    @Captor
    private ArgumentCaptor<List<Long>> planIdCaptor;

    @BeforeEach
    void setUp() {
        dbResponse = List.of(
                PlanAdminRule.builder().planId(12L).deptNbr(34).minReplItemUnits(22).replItemPieceRule(55).build(),
                PlanAdminRule.builder().planId(73L).deptNbr(23).minReplItemUnits(23).replItemPieceRule(56).build()
        );
    }

    @Test
    void test_getPlanAdminRulesShouldReturnPlanAdminRuleResponsesWhenPlanIdProvided() {
        when(planAdminRulesRespository.findAllById(anyList())).thenReturn(Collections.singletonList(dbResponse.get(0)));
        actual = planAdminRuleService.getPlanAdminRules(Collections.singletonList(12L));
        assertEquals(1, actual.size());
    }

    @Test
    void test_getPlanAdminRulesShouldReturnAllPlanAdminRuleResponsesWhenRequestIsEmpty() {
        when(planAdminRulesRespository.findAll()).thenReturn(dbResponse);
        actual = planAdminRuleService.getPlanAdminRules(Collections.emptyList());
        assertEquals(2, actual.size());
    }

    @Test
    void test_addPlanAdminRulesShouldAddData() {
        planAdminRuleRequest = PlanAdminRuleRequest.builder().planId(12L).deptNbr(34).minReplItemUnits(22).replItemPieceRule(55).build();
        List<PlanAdminRuleRequest> request = new ArrayList<>(Collections.singletonList(planAdminRuleRequest));
        planAdminRuleService.addPlanAdminRules(request);
        verify(planAdminRulesRespository, Mockito.times(1)).saveAll(planAdminRuleCaptor.capture());
        assertEquals(1, planAdminRuleCaptor.getValue().size());
    }

    @Test
    void test_deletePlanAdminRulesShouldDeleteData() {
        planAdminRuleService.deletePlanAdminRule(Collections.singletonList(12L));
        verify(planAdminRulesRespository, Mockito.times(1))
                .deleteAllById(planIdCaptor.capture());
        assertEquals(1, planIdCaptor.getValue().size());
    }

    @Test
    void test_deletePlanAdminRulesShouldPartialDeleteData() {
        planAdminRuleService.deletePlanAdminRule(Collections.singletonList(12L));
        verify(planAdminRulesRespository, Mockito.times(1))
                .deleteAllById(planIdCaptor.capture());
        assertEquals(1, planIdCaptor.getValue().size());
    }

    @Test
    void test_deletePlanAdminRulesShouldThrowExceptionIfPlanIdIsNotAvailable() {
        doThrow(RuntimeException.class).when(planAdminRulesRespository).deleteAllById(anySet());
        when(planAdminRulesRespository.findById(12L)).thenReturn(Optional.of(dbResponse.get(0)));
        List<Long> planIds = List.of(12L);
        assertThrows(CustomException.class, () -> planAdminRuleService.deletePlanAdminRule(planIds));
    }


    @Test
    void test_updatePlanAdminRulesShouldUpdateData() {
        planAdminRuleRequest = PlanAdminRuleRequest.builder().planId(12L).deptNbr(34).minReplItemUnits(22).replItemPieceRule(55).build();
        PlanAdminRuleRequest planAdminRuleRequest1 = PlanAdminRuleRequest.builder().
                planId(73L).deptNbr(23).minReplItemUnits(22).replItemPieceRule(55).build();
        List<PlanAdminRuleRequest> request = new ArrayList<>(Arrays.asList(planAdminRuleRequest, planAdminRuleRequest1));
        when(planAdminRulesRespository.findAllById(any())).thenReturn(List.of(PlanAdminRule.builder().planId(12L).deptNbr(34).minReplItemUnits(11).replItemPieceRule(44).build()));
        planAdminRuleService.updatePlanAdminRules(request);

        verify(planAdminRulesRespository, Mockito.times(1))
                .saveAll(planAdminRuleCaptor.capture());
        assertEquals(1, planAdminRuleCaptor.getValue().size());
        assertEquals(22, planAdminRuleCaptor.getValue().iterator().next().getMinReplItemUnits());
        assertEquals(55, planAdminRuleCaptor.getValue().iterator().next().getReplItemPieceRule());
    }
}
