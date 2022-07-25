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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fixturetype_rollup_id", referencedColumnName = "fixturetype_rollup_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private FixtureTypeRollUp fixtureTypeRollUp;

    @Column(name = "flow_strategy_code", nullable = false)
    private Integer flowStrategyCode;

    @JoinColumn(name = "flow_strategy_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = FpStrategyText.class, fetch = FetchType.LAZY)
    private FpStrategyText fpStrategyText;

    @Column(name="merch_method_code", nullable = false)
    private Integer merchMethodCode;

    @Column(name="merch_method_short_desc", nullable = false)
    private String merchMethodShortDesc;

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

    @OneToMany(mappedBy = "spFineLineChannelFixture", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SpStyleChannelFixture> spStyleChannelFixtures;
}
