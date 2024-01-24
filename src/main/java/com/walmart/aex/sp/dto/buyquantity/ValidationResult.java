package com.walmart.aex.sp.dto.buyquantity;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ValidationResult {
    Set<Integer> codes;
}
