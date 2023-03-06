package com.walmart.aex.sp.dto.commitmentreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RFAInitialSetBumpSetResponse {
	private String style_id;
	private String in_store_week;
	private String cc;
	private String merch_method;
	private String pack_id;
	private String size;
	private Integer initialpack_ratio;
	private Integer bumppack_ratio;
	private Integer store;
	private Integer is_quantity;
	private Integer bs_quantity;
	private String uuid;
	private String product_fineline;
	private Integer bumpPackNum;

}