package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Style {
    private String styleNbr;
   
    private Constraints constraints;
    private List<CustomerChoice> customerChoices;
}
