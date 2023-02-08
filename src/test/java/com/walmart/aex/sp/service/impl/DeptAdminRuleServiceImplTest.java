package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.dto.deptadminrule.ReplItemResponse;
import com.walmart.aex.sp.entity.DeptAdminRule;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Mock
    private BuyQtyProperties buyQtyProperties;

    @Captor
    private ArgumentCaptor<List<DeptAdminRule>> deptAdminRuleCaptor;
    @Captor
    private ArgumentCaptor<List<Integer>> deptIdCaptor;

    @BeforeEach
    void setUp() {
        dbResponse = List.of(
                DeptAdminRule.builder().deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build(),
                DeptAdminRule.builder().deptNbr(13).minReplItemUnits(23).replItemPieceRule(56).build()
        );
    }

    @Test
    void test_getDeptAdminRulesShouldReturnDeptAdminRuleResponsesWhenDeptNbrProvided() {
        when(deptAdminRuleRepository.findAllById(anyList())).thenReturn(Collections.singletonList(dbResponse.get(0)));
        actual = deptAdminRuleService.getDeptAdminRules(Collections.singletonList(12));
        assertEquals(1, actual.size());
    }

    @Test
    void test_getDeptAdminRulesShouldReturnAllDeptAdminRuleResponsesWhenRequestIsEmpty() {
        when(deptAdminRuleRepository.findAll()).thenReturn(dbResponse);
        actual = deptAdminRuleService.getDeptAdminRules(Collections.emptyList());
        assertEquals(2, actual.size());
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
        when(deptAdminRuleRepository.findById(12)).thenReturn(Optional.of(dbResponse.get(0)));
        deptAdminRuleService.deleteDeptAdminRules(Collections.singletonList(12));
        verify(deptAdminRuleRepository, Mockito.times(1))
                .deleteAllById(deptIdCaptor.capture());
        assertEquals(1, deptIdCaptor.getValue().size());
    }

    @Test
    void test_deleteAdminRulesShouldPartialDeleteData() {
        when(deptAdminRuleRepository.findById(12)).thenReturn(Optional.of(dbResponse.get(0)));
        deptAdminRuleService.deleteDeptAdminRules(Collections.singletonList(12));
        verify(deptAdminRuleRepository, Mockito.times(1))
                .deleteAllById(deptIdCaptor.capture());
        assertEquals(1, deptIdCaptor.getValue().size());
    }

    @Test
    void test_deleteAdminRulesShouldThrowExceptionIfDeptIdIsNotAvailable() {
        doThrow(RuntimeException.class).when(deptAdminRuleRepository).deleteAllById(anySet());
        when(deptAdminRuleRepository.findById(12)).thenReturn(Optional.of(dbResponse.get(0)));
        assertThrows(CustomException.class, () -> deptAdminRuleService.deleteDeptAdminRules(Collections.singletonList(12)));
    }

    @Test
    void test_deleteAdminRulesShouldLogErrorIfDeptIsNotExisted() {
        when(deptAdminRuleRepository.findById(12)).thenReturn(Optional.empty());
        deptAdminRuleService.deleteDeptAdminRules(Collections.singletonList(12));
    }

    @Test
    void test_updateAdminRulesShouldUpdateData() {
        deptAdminRuleRequest = DeptAdminRuleRequest.builder()
                .deptNbr(12).minReplItemUnits(22).replItemPieceRule(55).build();
        DeptAdminRuleRequest deptAdminRuleRequest2 = DeptAdminRuleRequest.builder()
                .deptNbr(13).minReplItemUnits(22).replItemPieceRule(55).build();
        List<DeptAdminRuleRequest> request = new ArrayList<>(Arrays.asList(deptAdminRuleRequest, deptAdminRuleRequest2));
        when(deptAdminRuleRepository.findById(12)).thenReturn(Optional.of(DeptAdminRule.builder().deptNbr(12).minReplItemUnits(11).replItemPieceRule(44).build()));
        deptAdminRuleService.updateAdminRules(request);

        verify(deptAdminRuleRepository, Mockito.times(1))
                .saveAll(deptAdminRuleCaptor.capture());
        assertEquals(1, deptAdminRuleCaptor.getValue().size());
        assertEquals(22, deptAdminRuleCaptor.getValue().iterator().next().getMinReplItemUnits());
        assertEquals(55, deptAdminRuleCaptor.getValue().iterator().next().getReplItemPieceRule());
    }

    @Test
    void test_getInitialThresholdShouldReturnReplItemPieceRule() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(deptAdminRuleRepository.findAllById(anyList())).thenReturn(Collections.singletonList(dbResponse.get(0)));
        Integer actual = deptAdminRuleService.getInitialThreshold(12L, 22);
        assertEquals(55, actual);
    }

    @Test
    void test_getInitialThresholdShouldReturnDefaultReplItemPieceRule() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(deptAdminRuleRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        Integer actual = deptAdminRuleService.getInitialThreshold(12L, 22);
        assertEquals(2, actual);
    }

    @Test
    void test_getInitialThresholdShouldReturnBuyQtyPropertiesReplItemPieceRule() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(buyQtyProperties.getInitialThreshold()).thenReturn(22);
        Integer actual = deptAdminRuleService.getInitialThreshold(33L, 22);
        assertEquals(22, actual);
    }

    @Test
    void test_getReplenishmentThresholdShouldReturnMinReplItemUnits() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(deptAdminRuleRepository.findAllById(anyList())).thenReturn(Collections.singletonList(dbResponse.get(0)));
        Integer actual = deptAdminRuleService.getReplenishmentThreshold(12L, 22);
        assertEquals(22, actual);
    }

    @Test
    void test_getReplenishmentThresholdShouldReturnDefaultMinReplItemUnits() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(deptAdminRuleRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        Integer actual = deptAdminRuleService.getReplenishmentThreshold(12L, 22);
        assertEquals(2500, actual);
    }

    @Test
    void test_getReplenishmentThresholdShouldReturnBuyQtyPropertiesMinReplItemUnits() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(buyQtyProperties.getReplenishmentThreshold()).thenReturn(150);
        Integer actual = deptAdminRuleService.getReplenishmentThreshold(33L, 22);
        assertEquals(150, actual);
    }

    @Test
    void test_getRepelItemRuleShouldReturnValuesFromCCM() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(buyQtyProperties.getInitialThreshold()).thenReturn(2);
        when(buyQtyProperties.getReplenishmentThreshold()).thenReturn(500);
        ReplItemResponse repelItemRule = deptAdminRuleService.getRepelItemRule(22L, 35);
        assertEquals(2, repelItemRule.getReplItemPieceRule());
        assertEquals(500, repelItemRule.getMinReplItemUnits());
    }

    @Test
    void test_getRepelItemRuleShouldReturnDefaultValuesIfDeptNbrDoesNotExist() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(deptAdminRuleRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        ReplItemResponse repelItemRule = deptAdminRuleService.getRepelItemRule(23L, 35);
        assertEquals(2, repelItemRule.getReplItemPieceRule());
        assertEquals(2500, repelItemRule.getMinReplItemUnits());
    }

    @Test
    void test_getRepelItemRuleShouldReturnValuesFromDB() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(DeptAdminRuleServiceImpl.class, "buyQtyProperties");
        field.setAccessible(true);
        field.set(deptAdminRuleService, buyQtyProperties);
        when(buyQtyProperties.getS3PlanIds()).thenReturn("22, 33, 44");
        when(deptAdminRuleRepository.findAllById(anyList())).thenReturn(Collections.singletonList(dbResponse.get(0)));
        ReplItemResponse repelItemRule = deptAdminRuleService.getRepelItemRule(23L, 35);
        assertEquals(55, repelItemRule.getReplItemPieceRule());
        assertEquals(22, repelItemRule.getMinReplItemUnits());
    }

}
