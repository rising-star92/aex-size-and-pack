package com.walmart.aex.sp.dto.replenishment.cons;

import com.walmart.aex.sp.entity.CcReplPackId;
import lombok.Data;

@Data
public class CcReplPackCons extends ReplVnPackAndWhPackCount {
    private CcReplPackId ccReplPackId;
}

