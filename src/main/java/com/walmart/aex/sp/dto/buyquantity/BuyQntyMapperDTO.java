package com.walmart.aex.sp.dto.buyquantity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyQntyMapperDTO {
    BuyQntyResponseDTO buyQntyResponseDTO;
    BuyQtyResponse response;
    HierarchyMetadata hierarchyMetadata;
    Integer requestFinelineNbr;
}
