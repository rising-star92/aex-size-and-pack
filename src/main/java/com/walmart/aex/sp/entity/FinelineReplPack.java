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
@Table(name = "rc_fl_replpk_fixtr_cons", schema = "dbo")
@Embeddable
public class FinelineReplPack
{
    @EmbeddedId
    @EqualsAndHashCode.Include
    FinelineReplPackId finelineReplPackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "channel_id", referencedColumnName = "channel_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fixturetype_rollup_id", referencedColumnName = "fixturetype_rollup_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private SubCatgReplPack subCatgReplPack;

    @Column(name="final_buy_units")
    private Integer finalBuyUnits;

    @Column(name="repl_units")
    private Integer replUnits;

    @Column(name="vendor_pack_cnt")
    private Integer vendorPackCnt;

    @Column(name="whse_pack_cnt")
    private Integer whsePackCnt;

    @Column(name="vnpk_whpk_ratio")
    private Double vnpkWhpkRatio;

    @Column(name="repl_pack_cnt")
    private Integer replPackCnt;

    @Column(name="fixturetype_rollup_name")
    private String fixtureTypeRollupName;

    @Column(name = "run_status_code")
    private Integer runStatusCode;

    @OneToMany(mappedBy = "finelineReplPack", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StyleReplPack> styleReplPack;
}
