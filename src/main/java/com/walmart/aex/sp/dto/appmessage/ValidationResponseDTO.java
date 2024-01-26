package com.walmart.aex.sp.dto.appmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponseDTO {
    private Integer code;
    private String type;
    private String message;
}
