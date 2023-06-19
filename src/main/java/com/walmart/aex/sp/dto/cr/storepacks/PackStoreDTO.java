package com.walmart.aex.sp.dto.cr.storepacks;

import lombok.Data;

@Data
public class PackStoreDTO 
{
	private String productFineline;
	private Integer fineline;
    private String cc;
    private String style_nbr;
    private Integer is_quantity;
    private Integer bs_quantity;
    private Integer store;
    private Integer clusterId;
    private float fixtureAllocation;
    private String fixtureType;
    private String packId;
    private Integer initialSetPackMultiplier;
    private Integer bumpSetPackMultiplier;
}
