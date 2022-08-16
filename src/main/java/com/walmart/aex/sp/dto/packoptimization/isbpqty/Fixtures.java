package com.walmart.aex.sp.dto.packoptimization.isbpqty;

import lombok.Data;

import java.util.List;

@Data
public class Fixtures {
    private String fixtureType;
    private String merchMethod;
    private List<Size> sizes;
}
