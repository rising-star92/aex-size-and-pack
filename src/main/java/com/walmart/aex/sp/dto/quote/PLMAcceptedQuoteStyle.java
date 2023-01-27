package com.walmart.aex.sp.dto.quote;

import lombok.Data;

import java.util.List;

@Data
public class PLMAcceptedQuoteStyle {
    private String styleNbr;
    private List<PLMAcceptedQuoteCc> plmAcceptedQuoteCcs;
}
