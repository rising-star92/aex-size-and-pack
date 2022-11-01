package com.walmart.aex.sp.dto.gql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.RfaWeeksResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.currentlineplan.Lvl3;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {
    private BuyQtyResponse getCcSizeClus;
    private APResponse getRFADataFromSizePack;
    private BuyQtyResponse getAllCcSizeClus;
    private BuyQtyResponse getFinelinesWithSizeAssociation;
    private BuyQtyResponse getStylesCCsWithSizeAssociation;
    private RfaWeeksResponse getRFAWeeksByFineline;
    private List<Lvl3> getLinePlanFinelines;
}
