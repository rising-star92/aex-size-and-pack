package com.walmart.aex.sp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private String status;
    private String message;
    private List<StatusResponse> statuses;

    public StatusResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
