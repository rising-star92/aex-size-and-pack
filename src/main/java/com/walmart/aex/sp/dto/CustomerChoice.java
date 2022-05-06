package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class CustomerChoice {
    private String ccId;
    private String colorName;
    private String channel;
    private UpdatedFields updatedFields;
    private Strategy strategy;

}
