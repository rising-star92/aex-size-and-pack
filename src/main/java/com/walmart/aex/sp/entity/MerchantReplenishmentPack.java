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
@Table(name = "merchcatg_replpk_cons", schema = "dbo")
@Embeddable
public class MerchantReplenishmentPack
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
    private Integer WhsePackCnt;

    @Column(name="vnpk_whpk_ratio")
    private Integer vnpkWhpkRatio;

    @OneToMany(mappedBy = "merchantReplenishmentPack", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubReplenishmentPack> subReplenishmentPack;
}
