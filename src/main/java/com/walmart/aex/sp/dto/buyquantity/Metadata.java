package com.walmart.aex.sp.dto.buyquantity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private List<ValidationMessage> validations;
    private List<Integer> validationCodes;
    private String status;
    private String lastUpdatedTs;
    private String userName;
}
