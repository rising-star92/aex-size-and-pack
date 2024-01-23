package com.walmart.aex.sp.dto.appmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMessageTextRequest {
    private Integer id;
    private Integer typeId;
    private String desc;
    private String longDesc;
}
