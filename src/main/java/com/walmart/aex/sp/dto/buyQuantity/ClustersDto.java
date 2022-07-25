package com.walmart.aex.sp.dto.buyQuantity;

import lombok.Data;

import java.util.List;

@Data
public class ClustersDto {
    private Integer clusterId;
    private List<SizeDto> sizeList;
}
