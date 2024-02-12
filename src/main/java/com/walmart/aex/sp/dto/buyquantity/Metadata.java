package com.walmart.aex.sp.dto.buyquantity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    private List<ValidationMessage> validations;
    private Set<Integer> validationCodes;
    private String status;
    private String lastUpdatedTs;
    private String userName;
}
