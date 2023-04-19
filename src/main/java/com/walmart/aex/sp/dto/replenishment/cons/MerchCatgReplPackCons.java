package com.walmart.aex.sp.dto.replenishment.cons;

import com.walmart.aex.sp.entity.MerchCatgReplPackId;
import lombok.Data;

@Data
public class MerchCatgReplPackCons extends ReplVnPackAndWhPackCount {
    private MerchCatgReplPackId merchCatgReplPackId;
}
