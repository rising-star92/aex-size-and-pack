package com.walmart.aex.sp.dto.buyquantity;

import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ValidationResult {
    List<Integer> codes;
}
