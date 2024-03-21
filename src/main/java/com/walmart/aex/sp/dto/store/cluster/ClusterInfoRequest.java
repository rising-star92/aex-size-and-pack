package com.walmart.aex.sp.dto.store.cluster;

import lombok.Data;

import java.util.Map;

@Data
public class ClusterInfoRequest {

    private String appType;

    private ClusterAttributes clusterAttributes;

}
