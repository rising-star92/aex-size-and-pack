package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CalculateQuantity {
    private Integer volumeGroupClusterId;
    private String fixtureType;
    private Float fixtureGroup;
}