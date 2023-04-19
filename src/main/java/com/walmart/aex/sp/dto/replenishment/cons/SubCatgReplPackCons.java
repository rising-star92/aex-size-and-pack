package com.walmart.aex.sp.dto.replenishment.cons;

import com.walmart.aex.sp.entity.SubCatgReplPackId;
import lombok.Data;

@Data
public class SubCatgReplPackCons extends ReplVnPackAndWhPackCount {
    private SubCatgReplPackId subCatgReplPackId;
}
