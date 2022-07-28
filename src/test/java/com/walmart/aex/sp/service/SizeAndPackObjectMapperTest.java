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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SizeAndPackObjectMapperTest {

    @InjectMocks
    SizeAndPackObjectMapper sizeAndPackObjectMapper;

    @Test
    void testAddLinePlanEvents() {
        PlanSizeAndPackDTO planSizeAndPackDTO = getPlanSizeAndPackDTOObj();
        Lvl1 lvl1 = planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        String channel = "store";
        Set<MerchCatPlan> merchCatPlanSet = sizeAndPackObjectMapper.setMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, channel);
        assertTrue(!merchCatPlanSet.isEmpty());
    }

    private PlanSizeAndPackDTO getPlanSizeAndPackDTOObj() {
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
