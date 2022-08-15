package com.walmart.aex.sp.dto.midas;

import lombok.Data;

import java.util.List;

@Data
public class MidasResponse {
   private Payload payload;
   private List<String> errors;
   private String status;
}
