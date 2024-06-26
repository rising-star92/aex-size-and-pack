package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class SubCatgReplPackId implements Serializable
{
    @Embedded
    private MerchCatgReplPackId merchCatgReplPackId;

    @Column(name="rpt_lvl_4_nbr",nullable = false)
    private Integer repTLvl4;

    public SubCatgReplPackId(MerchCatgReplPackId merchCatgReplPackId) {
        this.merchCatgReplPackId = merchCatgReplPackId;
    }
}
