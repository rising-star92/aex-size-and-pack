package com.walmart.aex.sp.dto.buyquantity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphQLRequest {
    private String query;
    private Map<String, Object> variables;
}
