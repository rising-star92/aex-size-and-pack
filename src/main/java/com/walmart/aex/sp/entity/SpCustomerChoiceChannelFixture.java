package com.walmart.aex.sp.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sp_cc_chan_fixtr", schema = "dbo")
@Embeddable
public class SpCustomerChoiceChannelFixture {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private SpCustomerChoiceChannelFixtureId spCustomerChoiceChannelFixtureId;

    @ManyToOne(fetch = FetchType.LAZY)
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
    @JsonIgnore
    private SpStyleChannelFixture spStyleChannelFixture;

    @JoinColumn(name = "flow_strategy_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = FpStrategyText.class, fetch = FetchType.LAZY)
    private FpStrategyText fpStrategyText;

    @Column(name = "flow_strategy_code")
    private Integer flowStrategyCode;

    @Column(name="merch_method_code")
    private Integer merchMethodCode;

    @Column(name="merch_method_short_desc")
    private String merchMethodShortDesc;

    @Column(name = "bump_pack_qty")
    private Integer bumpPackQty;

    @Column(name = "initial_set_qty")
    private Integer initialSetQty;

    @Column(name = "buy_qty")
    private Integer buyQty;

    @Column(name = "repln_qty")
    private Integer replnQty;

    @Column(name = "adj_repln_qty")
    private Integer adjReplnQty;

    @Column(name = "store_obj")
    private String storeObj;

    @Column(name = "bump_pack_cnt")
    private Integer bumpPackCnt;

    @OneToMany(mappedBy = "spCustomerChoiceChannelFixture", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSize;

}
