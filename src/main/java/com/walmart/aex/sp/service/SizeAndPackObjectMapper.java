//package com.walmart.aex.sp.service;
//
//import com.walmart.aex.sp.dto.*;
//import com.walmart.aex.sp.entity.*;
//import com.walmart.aex.sp.enums.ChannelType;
//
//public class SizeAndPackObjectMapper {
//
//
//    public MerchCatPlan mapMerchCatPlan(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, Fineline fineline) {
//
//        MerchCatPlan merchCatPlan = new MerchCatPlan();
//        MerchCatPlanId merchCatPlanId = new MerchCatPlanId();
//        ChannelText channelText = new ChannelText();
//        merchCatPlanId.setPlanId(request.getPlanId());
//        merchCatPlanId.setReptLvl0(request.getLvl0Nbr());
//        merchCatPlanId.setReptLvl1(lvl1.getLvl1Nbr());
//        merchCatPlanId.setReptLvl2(lvl2.getLvl2Nbr());
//        merchCatPlanId.setReptLvl3(lvl3.getLvl3Nbr());
//        merchCatPlan.setMerchCatPlanId(merchCatPlanId);
//        channelText.setChannelId(ChannelType.getChannelIdFromName(fineline.getChannel()));
//        channelText.setChannelDesc(fineline.getChannel());
//        merchCatPlan.setChannelText(channelText);
//        merchCatPlan.setRpt_lvl_0_gen_desc0(request.getLvl0Name());
//        merchCatPlan.setRpt_lvl_1_gen_desc1(lvl1.getLvl1Name());
//        merchCatPlan.setRpt_lvl_2_gen_desc2(lvl2.getLvl2Name());
//        merchCatPlan.setRpt_lvl_3_gen_desc3(lvl3.getLvl3Name());
//        return merchCatPlan;
//    }
//
//
//    public StylePlan mapStylePlan(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style) {
//
//        StylePlanId stylePlanId = new StylePlanId(request.getPlanId(), request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), Integer.valueOf(style.getStyleNbr()));
//        ChannelText channelText = new ChannelText(ChannelType.getChannelIdFromName(fineline.getChannel()), fineline.getChannel());
//        StylePlan stylePlan = new StylePlan();
//        stylePlan.setStylePlanId(stylePlanId);
//        stylePlan.setChannelText(channelText);
//        return stylePlan;
//    }
//
//    public SubCatPlan mapSubCatPlan(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline) {
//
//        SubCatPlanId subCatPlanId = new SubCatPlanId(request.getPlanId(), request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr());
//        ChannelText channelText = new ChannelText(ChannelType.getChannelIdFromName(fineline.getChannel()), fineline.getChannel());
//        SubCatPlan subCatPlan = new SubCatPlan();
//        subCatPlan.setChannelText(channelText);
//        subCatPlan.setSubCatPlanId(subCatPlanId);
//        subCatPlan.setRpt_lvl_0_gen_desc0(request.getLvl0Name());
//        subCatPlan.setRpt_lvl_1_gen_desc1(lvl1.getLvl1Name());
//        subCatPlan.setRpt_lvl_2_gen_desc2(lvl2.getLvl2Name());
//        subCatPlan.setRpt_lvl_3_gen_desc3(lvl3.getLvl3Name());
//        subCatPlan.setRpt_lvl_4_gen_desc4(lvl4.getLvl4Name());
//        return subCatPlan;
//    }
//
//    public CustChoice mapCustChoice(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style) {
//
//        CustChoiceId custChoiceId = new CustChoiceId(request.getPlanId(), request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), Integer.valueOf(style.getStyleNbr()), style.getCustomerChoices().get(0).getCcId());
//        ChannelText channelText = new ChannelText(ChannelType.getChannelIdFromName(fineline.getChannel()), fineline.getChannel());
//        CustChoice custChoice = new CustChoice();
//        custChoice.setChannelText(channelText);
//        custChoice.setCustChoiceId(custChoiceId);
//
//        return custChoice;
//    }
//}
