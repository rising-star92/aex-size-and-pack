package com.walmart.aex.sp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StylePlanId {

    @Column(name="plan_id", nullable = false)
    Long planId;
    @Column(name="rpt_lvl_0_nbr",nullable = false)
    Integer reptLvl0;
    @Column(name="rpt_lvl_1_nbr",nullable = false)
    Integer reptLvl1;
    @Column(name="rpt_lvl_2_nbr",nullable = false)
    Integer reptLvl2;
    @Column(name="rpt_lvl_3_nbr",nullable = false)
    Integer reptLvl3;
    @Column(name="rpt_lvl_4_nbr",nullable = false)
    Integer reptLvl4;
    @Column(name="fineline_nbr",nullable = false)
    Integer finelineNbr;
    @Column(name="style_nbr",nullable = false)
    Integer styleNbr;
}
