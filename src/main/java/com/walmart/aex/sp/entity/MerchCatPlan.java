package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "merchcatg_plan", schema = "dbo")
public class MerchCatPlan {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private MerchCatPlanId merchCatPlanId;

    @Column(name = "channel_id")
    private Integer channelId;

    @OneToMany(mappedBy = "merchCatPlan", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubCatPlan> subCatPlans;

    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
    private ChannelText channelText;

    @Column(name="rpt_lvl_0_gen_desc1",nullable = false)
    private String lvl0Desc;
    @Column(name="rpt_lvl_1_gen_desc1",nullable = false)
    private String lvl1Desc;
    @Column(name="rpt_lvl_2_gen_desc1",nullable = false)
    private String lvl2Desc;
    @Column(name="rpt_lvl_3_gen_desc1",nullable = false)
    private String lvl3Desc;
}