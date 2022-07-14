package com.walmart.aex.sp.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;



@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sp_cc_chan_fixtr_size", schema = "dbo")
@Embeddable
public class SpCustomerChoiceChannelFixtureSize {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private SpCustomerChoiceChannelFixtureId spCustomerChoiceChannelFixtureId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "channel_id", referencedColumnName = "channel_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "style_nbr", referencedColumnName = "style_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fixturetype_rollup_id", referencedColumnName = "fixturetype_rollup_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "customer_choice", referencedColumnName = "customer_choice", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture;


    @Column(name="ahs_size_desc", nullable = false)
    private Integer ahsSizeDesc;

    @JoinColumn(name = "flow_strategy_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = FpStrategyText.class, fetch = FetchType.LAZY)
    private FpStrategyText fpStrategyText;

    @Column(name="merch_method_code", nullable = false)
    private Integer merchMethodCode;

    @Column(name="merch_method_short_desc", nullable = false)
    private String merchMethodShortDesc;

    @Column(name = "avg_sp_pct", nullable = false)
    private Integer avgSpPct;


    @Column(name = "adj_sp_pct", nullable = false)
    private Integer adjSpPct;


    @Column(name = "bump_pack_qty", nullable = false)
    private Integer bumpPackQty;


    @Column(name = "initial_set_qty", nullable = false)
    private Integer initialSetQty;



    @Column(name = "buy_qty", nullable = false)
    private Integer buyQty;


    @Column(name = "repln_qty", nullable = false)
    private Integer replnQty;

    @Column(name = "adj_repln_qty", nullable = false)
    private Integer adjReplnQty;

    @Column(name = "store_obj", nullable = false)
    private String storeObj;


}
