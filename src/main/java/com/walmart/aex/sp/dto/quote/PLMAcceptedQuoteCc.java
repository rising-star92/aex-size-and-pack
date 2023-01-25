package com.walmart.aex.sp.dto.quote;

import lombok.Data;

import java.util.List;

@Data
public class PLMAcceptedQuoteCc {
    private String customerChoice;
    private List<PLMAcceptedQuote> plmAcceptedQuotes;
}
