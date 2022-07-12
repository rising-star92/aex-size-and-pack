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
public class SpCcSpBuyQuantityId implements Serializable{
	
	@Embedded
	private SpCcBuyQuantityId spCcBuyQuantityId;
	
	@Column(name="ahs_size_id", nullable=false)
	private String ahsSizeId;
	

}
