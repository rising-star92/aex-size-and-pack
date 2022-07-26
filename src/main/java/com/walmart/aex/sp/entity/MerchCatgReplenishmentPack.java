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
@Table(name = "merchcatg_replpk_cons", schema = "dbo")
@Embeddable
public class MerchCatgReplenishmentPack
{
    @EmbeddedId
    @EqualsAndHashCode.Include
    MerchantPackOptimizationID merchantPackOptimizationID;

    @Column(name="final_buy_units")
    private Integer finalBuyUnits;

    @Column(name="repl_units")
    private Integer replUnits;

    @Column(name="vendor_pack_cnt")
    private Integer vendorPackCnt;

    @Column(name="whse_pack_cnt")
    private Integer whsePackCnt;

    @Column(name="vnpk_whpk_ratio")
    private Integer vnpkWhpkRatio;

    @OneToMany(mappedBy = "merchCatgReplenishmentPack", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubCatgReplenishmentPack> subReplenishmentPack;
}
