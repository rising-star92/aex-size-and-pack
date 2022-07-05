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

	@Embedded
	private MerchantPackOptimizationID merchantPackOptimizationID;
	
	@Column(name="rpt_lvl_4_nbr",nullable = false)
    private Integer repTLvl4;
}
