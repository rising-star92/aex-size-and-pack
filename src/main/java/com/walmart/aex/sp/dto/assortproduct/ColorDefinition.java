package com.walmart.aex.sp.dto.assortproduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColorDefinition {
    private String cc;
    private String color_family_desc;
}
