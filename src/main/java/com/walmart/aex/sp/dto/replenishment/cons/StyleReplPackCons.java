package com.walmart.aex.sp.dto.replenishment.cons;

import com.walmart.aex.sp.entity.StyleReplPackId;
import lombok.Data;

@Data
public class StyleReplPackCons extends ReplVnPackAndWhPackCount {
    private StyleReplPackId styleReplPackId;
}
