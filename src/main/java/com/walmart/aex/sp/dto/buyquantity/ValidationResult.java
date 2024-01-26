package com.walmart.aex.sp.dto.buyquantity;

import lombok.*;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResult {
    Set<Integer> codes;
}
