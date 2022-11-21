package com.walmart.aex.sp.dto.isVolume;

import lombok.Data;

import java.util.List;

@Data
public class CustomerChoicesVolume {
    private String ccId;
    private List<IsPlan> isPlans;
}
