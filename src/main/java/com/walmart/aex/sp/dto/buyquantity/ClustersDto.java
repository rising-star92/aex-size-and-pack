package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class ClustersDto {
    private Integer clusterID;
    private List<SizeDto> sizes;
}
