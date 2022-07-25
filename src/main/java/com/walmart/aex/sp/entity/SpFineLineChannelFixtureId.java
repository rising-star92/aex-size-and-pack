package com.walmart.aex.sp.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class SpFineLineChannelFixtureId implements Serializable {

    @Embedded
    private FixtureTypeRollUpId fixtureTypeRollUpId;

    @Column(name="plan_id", nullable = false)
    private Long planId;
    @Column(name="rpt_lvl_0_nbr",nullable = false)
    private Integer lvl0Nbr;
    @Column(name="rpt_lvl_1_nbr",nullable = false)
    private Integer lvl1Nbr;
    @Column(name="rpt_lvl_2_nbr",nullable = false)
    private Integer lvl2Nbr;
    @Column(name="rpt_lvl_3_nbr",nullable = false)
    private Integer lvl3Nbr;
    @Column(name="rpt_lvl_4_nbr",nullable = false)
    private Integer lvl4Nbr;
    @Column(name="fineline_nbr",nullable = false)
    private Integer fineLineNbr;
    @Column(name="channel_id",nullable = false)
    private Integer channelId;
}
