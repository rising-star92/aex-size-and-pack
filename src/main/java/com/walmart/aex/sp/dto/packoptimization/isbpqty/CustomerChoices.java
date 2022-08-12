package com.walmart.aex.sp.dto.packoptimization.isbpqty;

import lombok.Data;

import java.util.List;

@Data
public class CustomerChoices {
    private String ccId;
    private List<Fixtures> fixtures;
}
