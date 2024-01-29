package com.walmart.aex.sp.dto.appmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMessageTextResponse {
    private Integer id;
    private String typeDesc;
    private String desc;
    private String longDesc;
}
