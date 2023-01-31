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
@Table(name = "style_plan", schema = "dbo")
public class StylePlan {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StylePlanId stylePlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "channel_id", referencedColumnName = "channel_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private FinelinePlan finelinePlan;

    @Column(name = "alt_style_desc")
    private String altStyleDesc;

    @OneToMany(mappedBy = "stylePlan", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustChoicePlan> custChoicePlans;
}
