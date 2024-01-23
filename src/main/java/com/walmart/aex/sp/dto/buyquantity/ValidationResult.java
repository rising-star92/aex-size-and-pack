package com.walmart.aex.sp.dto.buyquantity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationResult {
    List<Integer> codes;
}
