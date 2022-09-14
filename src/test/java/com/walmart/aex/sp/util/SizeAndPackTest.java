package com.walmart.aex.sp.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.walmart.aex.sp.entity.*;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
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

}
