package com.walmart.aex.sp.dto.packoptimization.packDescription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackDescriptionDetail {
    private String packId;
    private String altFinelineDesc;
    private String color;
}
