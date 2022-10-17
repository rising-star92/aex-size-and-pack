package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.commitmentreport.InitialSetPlan;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetResponseOne;
import com.walmart.aex.sp.dto.commitmentreport.Metrics;
import com.walmart.aex.sp.dto.commitmentreport.PackDetails;
import com.walmart.aex.sp.dto.commitmentreport.RFASizePackDataForCom;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetPlanMapper {

	public void mapInitialSetPlan(RFASizePackDataForCom rfaSizePackDataForCom, InitialSetResponseOne response, Integer fineline) {
		if (response.getStyleId() == null) {
			response.setFinelineNbr(fineline);
			response.setStyleId(rfaSizePackDataForCom.getStyle_id());
		}
		
		response.setInitialSetPlan(mapInitialSet(rfaSizePackDataForCom, response));
	}

	private List<InitialSetPlan> mapInitialSet(RFASizePackDataForCom rfaSizePackDataForCom, InitialSetResponseOne response) {
		List<InitialSetPlan> initialSetPlans = Optional.ofNullable(response.getInitialSetPlan()).orElse(new ArrayList<>());

		initialSetPlans.stream().filter(initialSetPlan -> rfaSizePackDataForCom.getIn_store_week().equals(initialSetPlan.getInStoreWeek())).findFirst()
				.ifPresentOrElse(initialSetPlan -> initialSetPlan.setPackDetails(mapInitialSetPack(rfaSizePackDataForCom, initialSetPlan)),
						() -> setInitialSetPlan(rfaSizePackDataForCom, initialSetPlans));
		return initialSetPlans;
	}

	private void setInitialSetPlan(RFASizePackDataForCom rfaSizePackDataForCom, List<InitialSetPlan> initialSetPlans) {
		InitialSetPlan initialSetPlan = new InitialSetPlan();
		initialSetPlan.setInStoreWeek(rfaSizePackDataForCom.getIn_store_week());
		initialSetPlan.setPackDetails(mapInitialSetPack(rfaSizePackDataForCom, initialSetPlan));
		initialSetPlans.add(initialSetPlan);
	}

	private List<PackDetails> mapInitialSetPack(RFASizePackDataForCom rfaSizePackDataForCom, InitialSetPlan initialSetPlan) {
		List<PackDetails> packDetails2 = Optional.ofNullable(initialSetPlan.getPackDetails()).orElse(new ArrayList<>());
		
		packDetails2.stream().filter(packDetails -> rfaSizePackDataForCom.getPack_id().equals(packDetails.getPackId())).findFirst()
		.ifPresentOrElse(packDetails -> packDetails.setMetrics(mapInitialSetPackMetrics(rfaSizePackDataForCom, packDetails)),
				() -> setInitialSetPack(rfaSizePackDataForCom, initialSetPlan));
		return packDetails2;
	}
	
	private void setInitialSetPack(RFASizePackDataForCom rfaSizePackDataForCom, InitialSetPlan initialSetPlan) {
		List<PackDetails> packDetails2 = Optional.ofNullable(initialSetPlan.getPackDetails()).orElse(new ArrayList<>());
		PackDetails paDetails = new PackDetails();
		paDetails.setPackId(rfaSizePackDataForCom.getPack_id());
		paDetails.setMetrics(mapInitialSetPackMetrics(rfaSizePackDataForCom,paDetails));
		packDetails2.add(paDetails);
	}
	
	private List<Metrics> mapInitialSetPackMetrics(RFASizePackDataForCom rfaSizePackDataForCom, PackDetails packDetails) {
		List<Metrics> metrics = Optional.ofNullable(packDetails.getMetrics()).orElse(new ArrayList<>());
		Metrics metrics2 = new Metrics();
		metrics2.setSize(rfaSizePackDataForCom.getSize());
		metrics2.setRatio(rfaSizePackDataForCom.getInitialpack_ratio());
		metrics2.setQuantity(rfaSizePackDataForCom.getIs_quantity());
		metrics2.setCcId(rfaSizePackDataForCom.getCc());
		metrics2.setMerchMethod(rfaSizePackDataForCom.getMerch_method());
		metrics.add(metrics2);
		return metrics;
	}
}
