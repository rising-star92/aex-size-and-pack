package com.walmart.aex.sp.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;

public class SizeAndPackTest {

    private static Integer lvl0Nbr = 50000;
    private static Integer lvl1Nbr = 34;
    private static Integer lvl2Nbr = 6419;
    private static Integer lvl3Nbr = 12228;
    private static Integer lvl4Nbr = 31507;
    private static Integer fineline1Nbr = 151;
    private static String style1Nbr = "151_2_23_001";
    private static String ccId = "151_2_23_001_001";

    public static List<MerchCatPlan> getMerchCatPlanSet() {
        List<MerchCatPlan> merchCatPlanSet = new ArrayList<>();
        merchCatPlanSet.add(getMerchCatPlan());
        return merchCatPlanSet;
    }

    public static MerchCatPlan getMerchCatPlan() {
        MerchCatPlan merchCatPlan = new MerchCatPlan();
        merchCatPlan.setMerchCatPlanId(getMerchCatPlanId());
        merchCatPlan.setSubCatPlans(getSubCatPlanSet());
        return merchCatPlan;
    }

    public static Set<SubCatPlan> getSubCatPlanSet() {

        Set<SubCatPlan> subCatPlanSet = new HashSet<>();
        subCatPlanSet.add(getSubCatPlan());
        return subCatPlanSet;
    }

    public static SubCatPlan getSubCatPlan() {
        SubCatPlan subCatPlan = new SubCatPlan();
        SubCatPlanId subCatPlanId = getSubCatPlanId();
        subCatPlan.setSubCatPlanId(subCatPlanId);
        subCatPlan.setFinelinePlans(getFinelinePlanSet());
        return subCatPlan;
    }



    public static Set<FinelinePlan> getFinelinePlanSet() {
        Set<FinelinePlan> finelinePlanSet = new HashSet<>();
        finelinePlanSet.add(getFinelinePlan());
        return finelinePlanSet;
    }

    public static FinelinePlan getFinelinePlan() {
        FinelinePlan finelinePlan = new FinelinePlan();
        FinelinePlanId finelinePlanId = getFinelinePlanId();
        finelinePlan.setFinelinePlanId(finelinePlanId);
        finelinePlan.setStylePlans(getStylePlanSet());
        finelinePlan.setAltFinelineName("Blue Soot Heather");
        return finelinePlan;
    }



    public static Set<StylePlan> getStylePlanSet() {
        Set<StylePlan> stylePlanSet = new HashSet<>();
        stylePlanSet.add(getStylePlan());
        return stylePlanSet;
    }

    public static StylePlan getStylePlan() {
        StylePlan stylePlan = new StylePlan();
        StylePlanId stylePlanId = getStylePlanId();
        stylePlan.setStylePlanId(stylePlanId);
        stylePlan.setCustChoicePlans(getCustChoicePlanSet());
        return stylePlan;
    }


    public static Set<CustChoicePlan> getCustChoicePlanSet() {
        Set<CustChoicePlan> custChoicePlanSet = new HashSet<>();
        custChoicePlanSet.add(getCustChoicePlan());
        return custChoicePlanSet;
    }

    public static CustChoicePlan getCustChoicePlan() {
        CustChoicePlan custChoicePlan = new CustChoicePlan();
        CustChoicePlanId custChoicePlanId = getCustChoicePlanId();
        custChoicePlan.setCustChoicePlanId(custChoicePlanId);
        return custChoicePlan;
    }

    private static CustChoicePlanId getCustChoicePlanId() {
        CustChoicePlanId custChoicePlanId = new CustChoicePlanId();
        custChoicePlanId.setStylePlanId(getStylePlanId());
        custChoicePlanId.setCcId(ccId);
        return custChoicePlanId;
    }

    private static StylePlanId getStylePlanId() {
        StylePlanId stylePlanId = new StylePlanId();
        stylePlanId.setFinelinePlanId(getFinelinePlanId());
        stylePlanId.setStyleNbr(style1Nbr);
        return stylePlanId;
    }
    private static FinelinePlanId getFinelinePlanId() {
        FinelinePlanId finelinePlanId = new FinelinePlanId();
        finelinePlanId.setSubCatPlanId(getSubCatPlanId());
        finelinePlanId.setFinelineNbr(fineline1Nbr);
        return finelinePlanId;
    }
    private static SubCatPlanId getSubCatPlanId() {
        SubCatPlanId subCatPlanId = new SubCatPlanId();
        subCatPlanId.setMerchCatPlanId(getMerchCatPlanId());
        subCatPlanId.setLvl4Nbr(lvl4Nbr);
        return subCatPlanId;
    }

    public static MerchCatPlanId getMerchCatPlanId(){
        MerchCatPlanId merchCatPlanId = new MerchCatPlanId();
        merchCatPlanId.setPlanId(346l);
        merchCatPlanId.setLvl0Nbr(lvl0Nbr);
        merchCatPlanId.setLvl1Nbr(lvl1Nbr);
        merchCatPlanId.setLvl2Nbr(lvl2Nbr);
        merchCatPlanId.setLvl3Nbr(lvl3Nbr);
        return merchCatPlanId;
    }

    public static PlanSizeAndPackDeleteDTO getPlanSizeAndPackDeleteDTO(Integer finelineNbr, String styleNbr, String ccId) {
        PlanSizeAndPackDeleteDTO request = new PlanSizeAndPackDeleteDTO();
        if(null!=finelineNbr || null!=styleNbr || null!=ccId){
            request.setStrongKey(getStrongKey(finelineNbr,styleNbr,ccId));
            request.setSizeAndPackPayloadDTO(getSizeAndPackPayloadDTO(finelineNbr,styleNbr,ccId));
        }
        return request;
    }

    private static PlanSizeAndPackDTO getSizeAndPackPayloadDTO(Integer finelineNbr, String styleNbr, String ccId) {
        PlanSizeAndPackDTO planSizeAndPackDTO = new PlanSizeAndPackDTO();
        planSizeAndPackDTO.setPlanId(12l);
        planSizeAndPackDTO.setLvl1List(getLvl1List(finelineNbr,styleNbr,ccId));
        return planSizeAndPackDTO;
    }

    private static List<Lvl1> getLvl1List(Integer finelineNbr, String styleNbr, String ccId) {
        List<Lvl1> lvl1List = new ArrayList<>();
        Lvl1 lvl1 = new Lvl1();
        lvl1.setLvl1Name("D34 - Womens Apparel");
        lvl1.setLvl1Nbr(lvl1Nbr);
        lvl1.setLvl2List(getLvl2List(finelineNbr,styleNbr,ccId));
        lvl1List.add(lvl1);
        return lvl1List;
    }

    private static List<Lvl2> getLvl2List(Integer finelineNbr, String styleNbr, String ccId) {
        List<Lvl2> lvl2List = new ArrayList<>();
        Lvl2 lvl2 = new Lvl2();
        lvl2.setLvl2Name("Plus Womens");
        lvl2.setLvl2Nbr(lvl2Nbr);
        lvl2.setLvl3List(getLvl3List(finelineNbr,styleNbr,ccId));
        lvl2List.add(lvl2);
        return lvl2List;
    }

    private static List<Lvl3> getLvl3List(Integer finelineNbr, String styleNbr, String ccId) {
        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(lvl3Nbr);
        lvl3.setLvl4List(getLvl4List(finelineNbr,styleNbr,ccId));
        lvl3List.add(lvl3);
        return lvl3List;
    }

    private static List<Lvl4> getLvl4List(Integer finelineNbr, String styleNbr, String ccId) {
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(lvl4Nbr);
        List<Fineline> fls = new ArrayList<>();
        fls.add(getFineline(finelineNbr, styleNbr, ccId));
        lvl4.setFinelines(fls);
        lvl4List.add(lvl4);
        return lvl4List;
    }

    private static StrongKey getStrongKey(Integer finelineNbr, String styleNbr, String ccId) {
        StrongKey strongKey = new StrongKey();
        strongKey.setPlanId(12l);
        strongKey.setPlanDesc("S1 - FYE 2024");
        strongKey.setLvl0Nbr(lvl0Nbr);
        strongKey.setLvl1Nbr(lvl1Nbr);
        strongKey.setLvl2Nbr(lvl2Nbr);
        strongKey.setLvl3Nbr(lvl3Nbr);
        strongKey.setLvl4Nbr(lvl4Nbr);
        strongKey.setFineline(getFineline(finelineNbr,styleNbr,ccId));
        return strongKey;
    }

    private static Fineline getFineline(Integer finelineNbr, String styleNbr, String ccId){
        Fineline fl = new Fineline();
        fl.setFinelineNbr(finelineNbr);
        if(styleNbr!=null)
            fl.setStyles(getStyles(styleNbr,ccId));
        return fl;
    }

    private static List<Style> getStyles(String styleNbr,String ccId) {
        List<Style> styles = new ArrayList<>();
        Style style = new Style();
        style.setStyleNbr(styleNbr);
        if(ccId!=null)
            style.setCustomerChoices(getCustChoice(ccId));
        styles.add(style);
        return styles;
    }

    private static List<CustomerChoice> getCustChoice(String ccId) {
        List<CustomerChoice> customerChoices = new ArrayList<>();
        CustomerChoice cc = new CustomerChoice();
        cc.setCcId(ccId);
        customerChoices.add(cc);
        return customerChoices;
    }
}
