package com.walmart.aex.sp.dto.gql;

import lombok.Data;

import java.util.List;

@Data
public class GraphQLResponse {
    private Payload data;
    private List<Error> errors;
}
