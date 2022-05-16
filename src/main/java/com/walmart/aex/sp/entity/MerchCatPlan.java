//package com.walmart.aex.sp.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "merchcatg_plan", schema = "dbo")
//public class MerchCatPlan {
//
//    @EmbeddedId
//MerchCatPlanId merchCatPlanId;
//@JoinColumn(name = "channel_id", insertable = false, updatable = false)
//@ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
//private ChannelText channelText;
//@Column(name="rpt_lvl_0_gen_desc0")
//String rpt_lvl_0_gen_desc0;
//@Column(name="rpt_lvl_1_gen_desc1")
//String rpt_lvl_1_gen_desc1;
//@Column(name="rpt_lvl_2_gen_desc2")
//String rpt_lvl_2_gen_desc2;
//@Column(name="rpt_lvl_3_gen_desc3")
//String rpt_lvl_3_gen_desc3;
//
//}
