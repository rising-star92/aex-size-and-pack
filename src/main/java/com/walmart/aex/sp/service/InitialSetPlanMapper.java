package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.commitmentreport.InitialSetPlan;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetResponseOne;
import com.walmart.aex.sp.dto.commitmentreport.Metrics;
import com.walmart.aex.sp.dto.commitmentreport.PackDetails;
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetPlanMapper {
	
	public void mapInitialSetPlan(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, InitialSetResponseOne response, Integer fineline) {
		//List<InitialSetResponseOne> initialRes = new ArrayList<>();
		if(response.getStyleId()==null) {
			setInitialSetStyle(rfaInitialSetBumpSetResponse,response,fineline);
			
		}else if(rfaInitialSetBumpSetResponse.getStyle_id().equalsIgnoreCase(response.getStyleId()) && response.getFinelineNbr().equals(fineline)) {
			response.setInitialSetPlan(mapInitialSet(rfaInitialSetBumpSetResponse, response));	
		}else {
			setInitialSetStyle(rfaInitialSetBumpSetResponse,response,fineline);
		}
		
	}
	
	private void setInitialSetStyle(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, InitialSetResponseOne response, Integer fineline) {
		response.setFinelineNbr(fineline);
		response.setStyleId(rfaInitialSetBumpSetResponse.getStyle_id());
		response.setInitialSetPlan(mapInitialSet(rfaInitialSetBumpSetResponse, response));
		
	}

	private List<InitialSetPlan> mapInitialSet(RFAInitialSetBumpSetResponse rfaSizePackDataForCom, InitialSetResponseOne response) {
		List<InitialSetPlan> initialSetPlans = Optional.ofNullable(response.getInitialSetPlan()).orElse(new ArrayList<>());

		initialSetPlans.stream().filter(initialSetPlan -> rfaSizePackDataForCom.getIn_store_week().equals(initialSetPlan.getInStoreWeek())).findFirst()
				.ifPresentOrElse(initialSetPlan -> initialSetPlan.setPackDetails(mapInitialSetPack(rfaSizePackDataForCom, initialSetPlan)),
						() -> setInitialSetPlan(rfaSizePackDataForCom, initialSetPlans));
		return initialSetPlans;
	}

	private void setInitialSetPlan(RFAInitialSetBumpSetResponse rfaSizePackDataForCom, List<InitialSetPlan> initialSetPlans) {
		InitialSetPlan initialSetPlan = new InitialSetPlan();
		initialSetPlan.setInStoreWeek(rfaSizePackDataForCom.getIn_store_week());
		initialSetPlan.setPackDetails(mapInitialSetPack(rfaSizePackDataForCom, initialSetPlan));
		initialSetPlans.add(initialSetPlan);
	}

	private List<PackDetails> mapInitialSetPack(RFAInitialSetBumpSetResponse rfaSizePackDataForCom, InitialSetPlan initialSetPlan) {
		List<PackDetails> packDetails2 = Optional.ofNullable(initialSetPlan.getPackDetails()).orElse(new ArrayList<>());
		
		packDetails2.stream().filter(packDetails -> rfaSizePackDataForCom.getPack_id().equals(packDetails.getPackId())).findFirst()
		.ifPresentOrElse(packDetails -> packDetails.setMetrics(mapInitialSetPackMetrics(rfaSizePackDataForCom, packDetails)),
				() -> setInitialSetPack(rfaSizePackDataForCom, packDetails2));
		return packDetails2;
	}
	
	private void setInitialSetPack(RFAInitialSetBumpSetResponse rfaSizePackDataForCom, List<PackDetails> packDetails) {
		PackDetails paDetails = new PackDetails();
		paDetails.setPackId(rfaSizePackDataForCom.getPack_id());
		paDetails.setMetrics(mapInitialSetPackMetrics(rfaSizePackDataForCom,paDetails));
		packDetails.add(paDetails);
	}
	
	private List<Metrics> mapInitialSetPackMetrics(RFAInitialSetBumpSetResponse rfaSizePackDataForCom, PackDetails packDetails) {
		List<Metrics> metrics = Optional.ofNullable(packDetails.getMetrics()).orElse(new ArrayList<>());
		Metrics metrics2 = new Metrics();
		metrics2.setSize(rfaSizePackDataForCom.getSize());
		if(packDetails.getPackId().startsWith("SP_is")) {
			metrics2.setRatio(rfaSizePackDataForCom.getInitialpack_ratio());
		}else {
			metrics2.setRatio(rfaSizePackDataForCom.getBumppack_ratio());
		}
		metrics2.setQuantity(rfaSizePackDataForCom.getIs_quantity());
		metrics2.setCcId(rfaSizePackDataForCom.getCc());
		metrics2.setMerchMethod(rfaSizePackDataForCom.getMerch_method());
		metrics.add(metrics2);
		return metrics;
	}
}
