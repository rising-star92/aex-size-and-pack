package com.walmart.aex.sp.dto.deptadminrule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReplItemResponse {
    private Integer replItemPieceRule;
    private Integer minReplItemUnits;
}
