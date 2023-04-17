package com.walmart.aex.sp.dto.replenishment.cons;

import com.walmart.aex.sp.entity.CcMmReplPackId;
import lombok.Data;

@Data
public class CcMmReplPackCons extends ReplVnPackAndWhPackCount {
    private CcMmReplPackId ccMmReplPackId;
}
