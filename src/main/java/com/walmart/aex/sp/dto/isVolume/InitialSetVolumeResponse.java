package com.walmart.aex.sp.dto.isVolume;

import lombok.Data;

import java.util.List;

@Data
public class InitialSetVolumeResponse {
    private int finelineNbr;
    private String styleId;
    private List<CustomerChoicesVolume> customerChoices;
}
