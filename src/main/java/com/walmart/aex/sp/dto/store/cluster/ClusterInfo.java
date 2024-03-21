package com.walmart.aex.sp.dto.store.cluster;

import lombok.Data;

import java.util.List;

@Data
public class ClusterInfo {

    private String clusterName;

    private List<Integer> storeList;

}
