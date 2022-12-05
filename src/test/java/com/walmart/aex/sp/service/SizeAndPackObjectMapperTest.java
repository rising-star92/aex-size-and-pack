package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl1;
import com.walmart.aex.sp.dto.planhierarchy.Lvl2;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.MerchCatPlan;
import com.walmart.aex.sp.entity.MerchCatPlanId;
import com.walmart.aex.sp.entity.SubCatPlan;
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class SizeAndPackObjectMapperTest {

    @InjectMocks
    private SizeAndPackObjectMapper sizeAndPackObjectMapper;

    @Mock
    private MerchCatPlanRepository merchCatPlanRepository;

    @Test
    void testAddLinePlanEvents() {
        PlanSizeAndPackDTO planSizeAndPackDTO = getPlanSizeAndPackDTOObj();
        Lvl1 lvl1 = planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        String channel = "store";
        Set<MerchCatPlan> merchCatPlanSet = sizeAndPackObjectMapper.setMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3);
        assertFalse(merchCatPlanSet.isEmpty());
    }

    @Test
    void testUpdateLinePlanEvents() {
        PlanSizeAndPackDTO planSizeAndPackDTO = getPlanSizeAndPackDTOObj();
        Lvl1 lvl1 = planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        String channel = "store";
        MerchCatPlan merchCatPlan1 = new MerchCatPlan();
        MerchCatPlanId merchCatPlanId1 = new MerchCatPlanId(planSizeAndPackDTO.getPlanId(),planSizeAndPackDTO.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), 1);
        merchCatPlan1.setMerchCatPlanId(merchCatPlanId1);
        merchCatPlan1.setLvl0Desc(planSizeAndPackDTO.getLvl0Name());
        merchCatPlan1.setLvl1Desc(lvl1.getLvl1Name());
        merchCatPlan1.setLvl2Desc(lvl2.getLvl2Name());
        merchCatPlan1.setLvl3Desc(lvl3.getLvl3Name());
        Set<SubCatPlan> subCatPlanSet1 =  sizeAndPackObjectMapper.setSubCatPlans(merchCatPlan1, planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl3.getLvl4List());
        merchCatPlan1.setSubCatPlans(subCatPlanSet1);
        MerchCatPlan merchCatPlan2 = new MerchCatPlan();
        MerchCatPlanId merchCatPlanId2 = new MerchCatPlanId(planSizeAndPackDTO.getPlanId(),planSizeAndPackDTO.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), 2);
        merchCatPlan2.setMerchCatPlanId(merchCatPlanId2);
        merchCatPlan2.setLvl0Desc(planSizeAndPackDTO.getLvl0Name());
        merchCatPlan2.setLvl1Desc(lvl1.getLvl1Name());
        merchCatPlan2.setLvl2Desc(lvl2.getLvl2Name());
        merchCatPlan2.setLvl3Desc(lvl3.getLvl3Name());
        Set<SubCatPlan> subCatPlanSet2 =  sizeAndPackObjectMapper.setSubCatPlans(merchCatPlan2, planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl3.getLvl4List());
        merchCatPlan2.setSubCatPlans(subCatPlanSet2);
        List<MerchCatPlan> merchCatPlanSetResult = new ArrayList<>();
        merchCatPlanSetResult.add(merchCatPlan1);
        merchCatPlanSetResult.add(merchCatPlan2);

        Mockito.when(merchCatPlanRepository.findMerchCatPlanByMerchCatPlanId_planIdAndMerchCatPlanId_lvl0NbrAndMerchCatPlanId_lvl1NbrAndMerchCatPlanId_lvl2NbrAndMerchCatPlanId_lvl3Nbr(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(merchCatPlanSetResult);
        Mockito.doNothing().when(merchCatPlanRepository).deleteById(merchCatPlanId2);
        Set<MerchCatPlan> merchCatPlanSet = sizeAndPackObjectMapper.updateMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3);
        assertFalse(merchCatPlanSet.isEmpty());
    }

     PlanSizeAndPackDTO getPlanSizeAndPackDTOObj() {
        PlanSizeAndPackDTO planSizeAndPackDTO = new PlanSizeAndPackDTO();
        planSizeAndPackDTO.setPlanId(100L);
        planSizeAndPackDTO.setPlanDesc("PlanDesc");
        planSizeAndPackDTO.setLvl0Name("Level 0");
        planSizeAndPackDTO.setLvl0Nbr(200);
        planSizeAndPackDTO.setLvl1List(getLv1List());
        return planSizeAndPackDTO;
    }

    private List<Lvl1> getLv1List() {
        List<Lvl1> lvl1List = new ArrayList<>();
        Lvl1 lvl1 = new Lvl1();
        lvl1.setLvl1Name("Level 1");
        lvl1.setLvl1Nbr(201);
        lvl1.setLvl2List(getLv2List());
        lvl1List.add(lvl1);
        return lvl1List;
    }

    private List<Lvl2> getLv2List() {
        List<Lvl2> lvl2List = new ArrayList<>();
        Lvl2 lvl2 = new Lvl2();
        lvl2.setLvl2Name("Level 2");
        lvl2.setLvl2Nbr(211);
        lvl2.setLvl3List(getLvl3List());
        lvl2List.add(lvl2);
        return lvl2List;
    }

    private List<Lvl3> getLvl3List() {
        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Name("Level 3");
        lvl3.setLvl3Nbr(221);
        lvl3.setLvl4List(getLvl4List());
        lvl3List.add(lvl3);
        return lvl3List;
    }

    private List<Lvl4> getLvl4List() {
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Name("Level 4");
        lvl4.setLvl4Nbr(231);
        lvl4.setFinelines(getFinelines());
        lvl4List.add(lvl4);
        return lvl4List;
    }

    private List<Fineline> getFinelines() {
        List<Fineline> finelines = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineName("fineline 1");
        fineline.setFinelineNbr(241);
        fineline.setChannel("store");
        fineline.setStyles(getStyles());
        finelines.add(fineline);
        return finelines;
    }

    private List<Style> getStyles() {
        List<Style> styles = new ArrayList<>();
        Style style = new Style();
        style.setStyleNbr("1263_20");
        style.setChannel("store");
        style.setCustomerChoices(getCustomerChoices());
        styles.add(style);
        return styles;
    }

    private List<CustomerChoice> getCustomerChoices() {
        List<CustomerChoice> customerChoices = new ArrayList<>();
        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setChannel("store");
        customerChoice.setCcId("34_4_21");
        customerChoice.setColorName("Red");
        customerChoices.add(customerChoice);
        return customerChoices;
    }
}
