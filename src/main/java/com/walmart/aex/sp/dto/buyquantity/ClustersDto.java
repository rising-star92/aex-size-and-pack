package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class ClustersDto {
    private Integer clusterId;
    private List<SizeDto> sizeList;
}
