package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;



import lombok.Data;


@Data
public class FineLinePackDto {
	 private Integer finelineNbr;
	  private List<CcPackDto> customerChoices;
}
