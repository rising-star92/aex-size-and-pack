package com.walmart.aex.sp.dto.replenishment.cons;

import com.walmart.aex.sp.entity.FinelineReplPackId;
import lombok.Data;

@Data
public class FinelineReplPackCons extends ReplVnPackAndWhPackCount {
    private FinelineReplPackId finelineReplPackId;
}
