package com.walmart.aex.sp.dto.replenishment.cons;

import com.walmart.aex.sp.entity.CcSpMmReplPack;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ReplenishmentCons {
    private MerchCatgReplPackCons merchCatgReplPackCons;
    private SubCatgReplPackCons subCatgReplPackCons;
    private FinelineReplPackCons finelineReplPackCons;
    private StyleReplPackCons styleReplPackCons;
    private CcReplPackCons ccReplPackCons;
    private CcMmReplPackCons ccMmReplPackCons;
    private Map<Integer, CcSpMmReplPack> ccSpMmReplPackConsMap = new HashMap<>();
}