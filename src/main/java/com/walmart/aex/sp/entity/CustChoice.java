//package com.walmart.aex.sp.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//
//@Getter
//@Setter
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "cc_plan", schema = "dbo")
//public class CustChoice {
//
//    @EmbeddedId
//    CustChoiceId custChoiceId;
//    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
//    @ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
//    private ChannelText channelText;
//}
