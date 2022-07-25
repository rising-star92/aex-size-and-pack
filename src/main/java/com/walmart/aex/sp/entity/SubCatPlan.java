package com.walmart.aex.sp.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subcatg_plan", schema = "dbo")
public class SubCatPlan {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private SubCatPlanId subCatPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private MerchCatPlan merchCatPlan;

    @Column(name = "channel_id")
    private Integer channelId;

    @Column(name="rpt_lvl_0_gen_desc0",nullable = false)
    private String lvl0Desc;

    @Column(name="rpt_lvl_1_gen_desc1",nullable = false)
    private String lvl1Desc;

    @Column(name="rpt_lvl_2_gen_desc2",nullable = false)
    private String lvl2Desc;

    @Column(name="rpt_lvl_3_gen_desc3",nullable = false)
    private String lvl3Desc;

    @Column(name="rpt_lvl_4_gen_desc4",nullable = false)
    private String lvl4Desc;

    @OneToMany(mappedBy = "subCatPlan", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FinelinePlan> finelinePlans;

    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
    private ChannelText channelText;
}
