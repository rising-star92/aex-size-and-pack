package com.walmart.aex.sp.dto.gql;

import com.walmart.aex.sp.dto.bqfp.RfaWeeksResponse;
import lombok.Data;

import java.util.List;

@Data
public class GraphQLRfaResponse {
    private Payload data;
    private List<Error> errors;
}
