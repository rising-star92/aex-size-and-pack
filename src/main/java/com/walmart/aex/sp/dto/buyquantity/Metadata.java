package com.walmart.aex.sp.dto.buyquantity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class Metadata {
    private List<ValidationMessage> validations;
    private String status;
    private String lastUpdatedTs;
    private String userName;
}
