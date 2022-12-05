package com.walmart.aex.sp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
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
public class StylePackOptimizationID implements Serializable{
	
	@Embedded
	private FineLinePackOptimizationID finelinePackOptimizationID;
	
	@Column(name="style_nbr", nullable=false)
	@Convert(converter = CharConverter.class)
	private String styleNbr;

}
