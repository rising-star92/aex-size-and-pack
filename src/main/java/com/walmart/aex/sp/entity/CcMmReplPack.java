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
@Table(name = "rc_cc_mm_replpk_fixtr_cons", schema = "dbo")
@Embeddable
public class CcMmReplPack
{
    @EmbeddedId
    @EqualsAndHashCode.Include
    CcMmReplPackId ccMmReplPackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "style_nbr", referencedColumnName = "style_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "customer_choice", referencedColumnName = "customer_choice", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "channel_id", referencedColumnName = "channel_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fixturetype_rollup_id", referencedColumnName = "fixturetype_rollup_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private CcReplPack ccReplPack;

    @Column(name="color_family_desc")
    private String colorFamilyDesc;

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

    @Column(name="merch_method_short_desc")
    private String merchMethodDesc;

    @Column(name="replen_obj")
    private String replenObj;

    @Column(name = "app_message_obj")
    private String messageObj;

    @OneToMany(mappedBy = "ccMmReplPack", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CcSpMmReplPack> ccSpMmReplPack;
}
