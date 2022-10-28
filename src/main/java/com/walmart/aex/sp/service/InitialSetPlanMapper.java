package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.commitmentreport.InitialSetPlan;
import com.walmart.aex.sp.dto.commitmentreport.InitialBumpSetResponse;
import com.walmart.aex.sp.dto.commitmentreport.Metrics;
import com.walmart.aex.sp.dto.commitmentreport.PackDetails;
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponse;
import com.walmart.aex.sp.dto.commitmentreport.IntialSetStyle;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetPlanMapper {
	
	public void mapInitialSetPlan(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, InitialBumpSetResponse response, Integer fineline) {
		response.setFinelineNbr(fineline);
		response.setIntialSetStyles(mapInitialSetStyle(rfaInitialSetBumpSetResponse, response));
		
	}
	
	private List<IntialSetStyle> mapInitialSetStyle(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, InitialBumpSetResponse response) {
		List<IntialSetStyle> styles = Optional.ofNullable(response.getIntialSetStyles()).orElse(new ArrayList<>());

		styles.stream().filter(styleObj -> rfaInitialSetBumpSetResponse.getStyle_id().equals(styleObj.getStyleId())).findFirst()
				.ifPresentOrElse(styleObj -> styleObj.setInitialSetPlan(mapInitialSet(rfaInitialSetBumpSetResponse, styleObj)),
						() -> setInitialSetStyle(rfaInitialSetBumpSetResponse, styles));
		return styles;
	}
	
	private void setInitialSetStyle(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, List<IntialSetStyle> styles) {
		IntialSetStyle style = new IntialSetStyle();
		style.setStyleId(rfaInitialSetBumpSetResponse.getStyle_id());
		style.setInitialSetPlan(mapInitialSet(rfaInitialSetBumpSetResponse, style));
		styles.add(style);
	}
	
	private List<InitialSetPlan> mapInitialSet(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, IntialSetStyle style) {
		List<InitialSetPlan> initialSetPlans = Optional.ofNullable(style.getInitialSetPlan()).orElse(new ArrayList<>());

		initialSetPlans.stream().filter(initialSetPlan -> rfaInitialSetBumpSetResponse.getIn_store_week().equals(initialSetPlan.getInStoreWeek())).findFirst()
				.ifPresentOrElse(initialSetPlan -> initialSetPlan.setPackDetails(mapInitialSetPack(rfaInitialSetBumpSetResponse, initialSetPlan)),
						() -> setInitialSetPlan(rfaInitialSetBumpSetResponse, initialSetPlans));
		return initialSetPlans;
	}

	private void setInitialSetPlan(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, List<InitialSetPlan> initialSetPlans) {
		InitialSetPlan initialSetPlan = new InitialSetPlan();
		initialSetPlan.setInStoreWeek(rfaInitialSetBumpSetResponse.getIn_store_week());
		initialSetPlan.setPackDetails(mapInitialSetPack(rfaInitialSetBumpSetResponse, initialSetPlan));
		initialSetPlans.add(initialSetPlan);
	}

	private List<PackDetails> mapInitialSetPack(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, InitialSetPlan initialSetPlan) {
		List<PackDetails> packDetails2 = Optional.ofNullable(initialSetPlan.getPackDetails()).orElse(new ArrayList<>());
		
		packDetails2.stream().filter(packDetails -> rfaInitialSetBumpSetResponse.getPack_id().equals(packDetails.getPackId())).findFirst()
		.ifPresentOrElse(packDetails -> packDetails.setMetrics(mapInitialSetPackMetrics(rfaInitialSetBumpSetResponse, packDetails)),
				() -> setInitialSetPack(rfaInitialSetBumpSetResponse, packDetails2));
		return packDetails2;
	}
	
	private void setInitialSetPack(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, List<PackDetails> packDetails) {
		PackDetails paDetails = new PackDetails();
		paDetails.setPackId(rfaInitialSetBumpSetResponse.getPack_id());
		paDetails.setMetrics(mapInitialSetPackMetrics(rfaInitialSetBumpSetResponse,paDetails));
		packDetails.add(paDetails);
	}
	
	private List<Metrics> mapInitialSetPackMetrics(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, PackDetails packDetails) {
		List<Metrics> metrics = Optional.ofNullable(packDetails.getMetrics()).orElse(new ArrayList<>());
		Metrics metrics2 = new Metrics();
		metrics2.setSize(rfaInitialSetBumpSetResponse.getSize());
		if(packDetails.getPackId().startsWith("SP_is")) {
			metrics2.setRatio(rfaInitialSetBumpSetResponse.getInitialpack_ratio());
		}else {
			metrics2.setRatio(rfaInitialSetBumpSetResponse.getBumppack_ratio());
		}
		metrics2.setQuantity(rfaInitialSetBumpSetResponse.getIs_quantity());
		metrics2.setCcId(rfaInitialSetBumpSetResponse.getCc());
		metrics2.setMerchMethod(rfaInitialSetBumpSetResponse.getMerch_method());
		metrics.add(metrics2);
		return metrics;
	}
}