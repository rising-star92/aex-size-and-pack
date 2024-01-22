package com.walmart.aex.sp.entity;


import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sp_fl_chan_fixtr", schema = "dbo")
@Embeddable
public class SpFineLineChannelFixture {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private SpFineLineChannelFixtureId spFineLineChannelFixtureId;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "fixturetype_rollup_id", referencedColumnName = "fixturetype_rollup_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private FixtureTypeRollUp fixtureTypeRollUp;

    @Column(name = "flow_strategy_code")
    private Integer flowStrategyCode;

    @JoinColumn(name = "flow_strategy_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = FpStrategyText.class, fetch = FetchType.LAZY)
    private FpStrategyText fpStrategyText;

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

    @Column(name = "app_message_obj")
    private String messageObj;

    @OneToMany(mappedBy = "spFineLineChannelFixture", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SpStyleChannelFixture> spStyleChannelFixtures;
}
