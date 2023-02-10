package com.walmart.aex.sp.dto.isVolume;

import lombok.Data;

@Data
public class VolumeClusterDTO {
    private String productFineline;
    private String cc;
    private String style_nbr;
    private Integer is_quantity;
    private Integer bs_quantity;
    private Integer store;
    private Integer clusterId;
    private Integer in_store_week;
    private float fixtureAllocation;
    private String fixtureType;
}
