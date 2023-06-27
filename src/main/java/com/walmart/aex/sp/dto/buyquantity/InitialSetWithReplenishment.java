package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitialSetWithReplenishment {
    private List<Replenishment> replenishments;
    private double isQty;
    private double perStoreQty;
    private List<Integer> storeList;

}
