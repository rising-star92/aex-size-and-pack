package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Fineline {
    private Integer finelineNbr;
    private String finelineName;
    private String channel;
    private String traitChoice;
    private UpdatedFields updatedFields;
    private Strategy strategy;
    private List<Style> styles;
}
