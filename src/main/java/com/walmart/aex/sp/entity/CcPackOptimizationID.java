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
public class CcPackOptimizationID implements Serializable{

	@Embedded
	private StylePackOptimizationID stylePackOptimizationID;
	
	@Column(name="customer_choice", nullable=false)
	@Convert(converter = CharConverter.class)
	private String customerChoice;
}
