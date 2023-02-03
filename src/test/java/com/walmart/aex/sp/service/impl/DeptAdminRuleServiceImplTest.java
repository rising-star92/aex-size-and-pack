package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.entity.DeptAdminRule;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.DeptAdminRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeptAdminRuleServiceImplTest {

    @InjectMocks
    private DeptAdminRuleServiceImpl deptAdminRuleService;

    @Mock
    private DeptAdminRuleRepository deptAdminRuleRepository;

    private List<DeptAdminRule> dbResponse;
    private DeptAdminRuleRequest deptAdminRuleRequest;
    private List<DeptAdminRuleResponse> actual;
    @Captor
    private ArgumentCaptor<List<DeptAdminRule>> deptAdminRuleCaptor;
    @Captor
    private ArgumentCaptor<Set<Integer>> deptIdCaptor;

    @BeforeEach
    void setUp() {
        dbResponse = List.of(
                DeptAdminRule.builder().deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build(),
                DeptAdminRule.builder().deptNbr(13).minReplItemUnits(23).replItemPieceRule(56).build()
        );
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenDeptNbrAndReplItemPieceRuleAndMinReplItemUnitsProvided() {
        when(deptAdminRuleRepository.findByDeptNbrAndReplItemPieceRuleAndMinReplItemUnits(anyInt(), anyInt(), anyInt())).thenReturn(dbResponse.get(0));
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenDeptNbrAndReplItemPieceRuleProvided() {
        when(deptAdminRuleRepository.findByDeptNbrAndReplItemPieceRule(anyInt(), anyInt())).thenReturn(dbResponse.get(0));
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().deptNbr(12).replItemPieceRule(55).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenDeptNbrAndMinReplItemUnitsProvided() {
        when(deptAdminRuleRepository.findByDeptNbrAndMinReplItemUnits(anyInt(), anyInt())).thenReturn(dbResponse.get(0));
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().deptNbr(12).minReplItemUnits(22).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenReplItemPieceRuleAndMinReplItemUnitsProvided() {
        when(deptAdminRuleRepository.findByReplItemPieceRuleAndMinReplItemUnits(anyInt(), anyInt())).thenReturn(dbResponse.get(0));
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().replItemPieceRule(55).minReplItemUnits(22).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenReplItemPieceRuleProvided() {
        when(deptAdminRuleRepository.findByReplItemPieceRule(anyInt())).thenReturn(dbResponse.get(0));
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().replItemPieceRule(55).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenDeptNbrProvided() {
        when(deptAdminRuleRepository.findByDeptNbr(anyInt())).thenReturn(dbResponse.get(0));
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().deptNbr(12).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenMinReplItemUnitsProvided() {
        when(deptAdminRuleRepository.findByMinReplItemUnits(anyInt())).thenReturn(dbResponse.get(0));
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().minReplItemUnits(22).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnAllDeptAdminRuleResponsesWhenRequestIsEmpty() {
        when(deptAdminRuleRepository.findAll()).thenReturn(dbResponse);
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(2, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnEmptyDeptAdminRuleResponsesWhenMinReplItemUnitsIsNotAvailableInDB() {
        when(deptAdminRuleRepository.findByMinReplItemUnits(anyInt())).thenReturn(null);
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().minReplItemUnits(22).build();
        actual = deptAdminRuleService.getDeptAdminRules(deptAdminRuleRequest);
        assertEquals(0, actual.size());
    }

    @Test
    void test_addAdminRulesShouldAddData() {
        deptAdminRuleRequest = DeptAdminRuleRequest.builder().deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build();
        List<DeptAdminRuleRequest> request = new ArrayList<>(Collections.singletonList(deptAdminRuleRequest));
        deptAdminRuleService.addAdminRules(request);
        verify(deptAdminRuleRepository, Mockito.times(1)).saveAll(deptAdminRuleCaptor.capture());
        assertEquals(1, deptAdminRuleCaptor.getValue().size());
    }

    @Test
    void test_deleteAdminRulesShouldDeleteData() {
        deptAdminRuleRequest = DeptAdminRuleRequest.builder()
                .deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build();
        List<DeptAdminRuleRequest> request = new ArrayList<>(Collections.singletonList(deptAdminRuleRequest));
        deptAdminRuleService.deleteDeptAdminRules(request);
        verify(deptAdminRuleRepository, Mockito.times(1))
                .deleteAllById(deptIdCaptor.capture());
        assertEquals(1, deptIdCaptor.getValue().size());
    }

    @Test
    void test_deleteAdminRulesShouldThrowExceptionIfDeptIdIsNotAvailable() {
        deptAdminRuleRequest = DeptAdminRuleRequest.builder()
                .deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build();
        List<DeptAdminRuleRequest> request = new ArrayList<>(Collections.singletonList(deptAdminRuleRequest));
        doThrow(RuntimeException.class).when(deptAdminRuleRepository).deleteAllById(anySet());
        assertThrows(CustomException.class, () -> deptAdminRuleService.deleteDeptAdminRules(request));
    }

    @Test
    void test_updateAdminRulesShouldUpdateData() {
        deptAdminRuleRequest = DeptAdminRuleRequest.builder()
                .deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build();
        DeptAdminRuleRequest deptAdminRuleRequest2 = DeptAdminRuleRequest.builder()
                .deptNbr(13).minReplItemUnits(22).replItemPieceRule(55).build();
        List<DeptAdminRuleRequest> request = new ArrayList<>(Arrays.asList(deptAdminRuleRequest, deptAdminRuleRequest2));
        when(deptAdminRuleRepository.findByDeptNbr(12)).thenReturn(DeptAdminRule.builder().deptNbr(12).minReplItemUnits(11).replItemPieceRule(44).build());
        deptAdminRuleService.updateAdminRules(request);

        verify(deptAdminRuleRepository, Mockito.times(1))
                .saveAll(deptAdminRuleCaptor.capture());
        assertEquals(1, deptAdminRuleCaptor.getValue().size());
        assertEquals(22, deptAdminRuleCaptor.getValue().iterator().next().getMinReplItemUnits());
        assertEquals(55, deptAdminRuleCaptor.getValue().iterator().next().getReplItemPieceRule());
    }

}
