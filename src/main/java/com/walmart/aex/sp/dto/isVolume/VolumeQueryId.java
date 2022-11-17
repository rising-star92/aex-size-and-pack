package com.walmart.aex.sp.dto.isVolume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class VolumeQueryId {
    private String cc;
    private int clusterId;
    private int inStoreWeek;

}
