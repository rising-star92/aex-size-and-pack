//package com.walmart.aex.sp.entity;
//
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
//@Table(name = "subcatg_plan", schema = "dbo")
//public class SubCatPlan {
//
//    @EmbeddedId
//    SubCatPlanId subCatPlanId;
//    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
//    @ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
//    private ChannelText channelText;
//    @Column(name="rpt_lvl_0_gen_desc0",nullable = false)
//    String rpt_lvl_0_gen_desc0;
//    @Column(name="rpt_lvl_1_gen_desc1",nullable = false)
//    String rpt_lvl_1_gen_desc1;
//    @Column(name="rpt_lvl_2_gen_desc2",nullable = false)
//    String rpt_lvl_2_gen_desc2;
//    @Column(name="rpt_lvl_3_gen_desc3",nullable = false)
//    String rpt_lvl_3_gen_desc3;
//    @Column(name="rpt_lvl_4_gen_desc4",nullable = false)
//    String rpt_lvl_4_gen_desc4;
//}
