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
public class FineLinePackOptimizationID implements Serializable{
	
	@Embedded
	private SubCatgPackOptimizationID subCatgPackOptimizationID;
	
	@Column(name="fineline_nbr", nullable=false)
	private Integer finelineNbr;

}
