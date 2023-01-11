package com.walmart.aex.sp.dto.packoptimization.sourcingFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Address {
    @JsonProperty("AddressLine1")
    private String addressLine1;
    @JsonProperty("AddressLine2")
    private String addressLine2;
    @JsonProperty("AddressLine3")
    private String addressLine3;
    @JsonProperty("AddressLine4")
    private String addressLine4;
    @JsonProperty("City")
    private String city;
    @JsonProperty("State")
    private String state;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("Zip")
    private String zip;
    @JsonProperty("CompleteAddress")
    private String completeAddress;
}
