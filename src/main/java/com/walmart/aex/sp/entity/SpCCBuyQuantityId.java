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
public class SpCCBuyQuantityId implements Serializable{
	
	@Embedded
	private SpStyleBuyQuantityId spStyleBuyQuantityId;
	
	@Column(name="customer_choice", nullable=false)
	private String customerChoice;
	

}
