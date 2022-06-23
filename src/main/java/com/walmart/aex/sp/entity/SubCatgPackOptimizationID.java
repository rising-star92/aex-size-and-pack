package com.walmart.aex.sp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class SubCatgPackOptimizationID implements Serializable{

	/*@Embedded
	private MerchantPackOptimizationID merchantPackOptimizationID;*/
	
	@Column(name="plan_id", nullable = false)
    private Long planId;
    @Column(name="rpt_lvl_0_nbr",nullable = false)
    private Integer repTLvl0;
    @Column(name="rpt_lvl_1_nbr",nullable = false)
    private Integer repTLvl1;
    @Column(name="rpt_lvl_2_nbr",nullable = false)
    private Integer repTLvl2;
    @Column(name="rpt_lvl_3_nbr",nullable = false)
    private Integer repTLvl3;
	
	@Column(name="rpt_lvl_4_nbr",nullable = false)
    private Integer repTLvl4;
}
