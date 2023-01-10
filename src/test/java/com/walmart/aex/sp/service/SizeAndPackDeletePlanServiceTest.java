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
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import com.walmart.aex.sp.util.SizeAndPackTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

public class SizeAndPackDeletePlanServiceTest {

    @InjectMocks
    private SizeAndPackDeletePlanService sizeAndPackDeletePlanService;

    @Mock
    private MerchCatPlanRepository merchCatPlanRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateMerchCatPlan() {
        //Arrange
        PlanSizeAndPackDTO request = new PlanSizeAndPackDTO();
        request.setPlanId(471l);
        request.setLvl0Nbr(50000);

        List<Lvl1> lvl1List = new ArrayList<>();
        Lvl1 lvl1 = new Lvl1();
        lvl1.setLvl1Nbr(34);
        lvl1.setLvl1Name("D34 - Womens Apparel");
        List<Lvl2> lvl2List = new ArrayList<>();
        Lvl2 lvl2 = new Lvl2();
        lvl2.setLvl2Nbr(6419);
        lvl2.setLvl2Name("Plus Womens");
        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(12228);
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(31507);
        Fineline fineline = new Fineline();
        List<Fineline> finelineList = new ArrayList<>();
        fineline.setFinelineNbr(151);
        fineline.setFinelineName("Women Hoodie");
        finelineList.add(fineline);
        Style style = new Style();
        style.setStyleNbr("151_2_23_001");

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("151_2_23_001_001");
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));
        lvl4.setFinelines(finelineList);
        lvl4List.add(lvl4);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        lvl2.setLvl3List(lvl3List);
        lvl2List.add(lvl2);
        lvl1.setLvl2List(lvl2List);
        lvl1List.add(lvl1);
        request.setLvl1List(lvl1List);
        List<MerchCatPlan> merchCatPlans = SizeAndPackTest.getMerchCatPlanSet();
        doReturn(merchCatPlans)
                .when(merchCatPlanRepository).findMerchCatPlanByMerchCatPlanId_planIdAndMerchCatPlanId_lvl0NbrAndMerchCatPlanId_lvl1NbrAndMerchCatPlanId_lvl2NbrAndMerchCatPlanId_lvl3Nbr(471l, 50000, 34,6419,12228);
        //Act
        Set<MerchCatPlan> merchCatPlanSet= sizeAndPackDeletePlanService.updateMerchCatPlan(request,lvl1,lvl2,lvl3,fineline,merchCatPlanRepository);
        assertEquals(0, merchCatPlanSet.size());
    }


}
