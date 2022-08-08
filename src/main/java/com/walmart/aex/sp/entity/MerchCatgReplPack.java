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
@Table(name = "rc_merchcatg_replpk_fixtr_cons", schema = "dbo")
@Embeddable
public class MerchCatgReplPack
{
    @EmbeddedId
    @EqualsAndHashCode.Include
    MerchCatgReplPackId merchCatgReplPackId;

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
    
    @Column(name="fixturetype_rollup_name", nullable = false)
    private String fixtureTypeRollupName;

    @JoinColumn(name = "run_status_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = RunStatusText.class, fetch = FetchType.LAZY)
    private RunStatusText runStatusText;

    @OneToMany(mappedBy = "merchCatgReplPack", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubCatgReplPack> subReplPack;
}
