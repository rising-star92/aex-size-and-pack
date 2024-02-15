package com.walmart.aex.sp.dto.buyquantity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HierarchyMetadata {
    Metadata finelineMetadata;
    Metadata styleMetadata;
    Metadata ccMetadata;
}
