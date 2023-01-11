package com.walmart.aex.sp.dto.packoptimization.sourcingFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FactoryDetailsDTO {
    @JsonProperty("FactoryId")
    private Integer factoryId;
    @JsonProperty("FactoryName")
    private String factoryName;
    @JsonProperty("Address")
    private Address address;
    @JsonProperty("Audit")
    private List<Audit> audits;
    @JsonProperty("OrangeCount")
    private Integer orangeCount;
}
