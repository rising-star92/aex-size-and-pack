package com.walmart.aex.sp.dto.cr.storepacks;

import lombok.Data;

@Data
public class PackStoreDTO 
{
	private String productFineline;
	private Integer fineline;
    private String cc;
    private String styleNbr;
    private Integer isQuantity;
    private Integer bsQuantity;
    private Integer store;
    private Integer clusterId;
    private float fixtureAllocation;
    private String fixtureType;
    private String packId;
    private Integer initialSetPackMultiplier;
    private Integer bumpSetPackMultiplier;
    private String merchMethod;
}
