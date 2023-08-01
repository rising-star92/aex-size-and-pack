package com.walmart.aex.sp.dto.replenishment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;

@Data
@AllArgsConstructor
public class DCInboundWorkbookResponse {
   private String fileName;
   private Workbook workbook;
}
