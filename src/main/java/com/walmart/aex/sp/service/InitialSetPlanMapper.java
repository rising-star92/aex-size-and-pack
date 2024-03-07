package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.walmart.aex.sp.dto.StoreClusterMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.commitmentreport.InitialSetPlan;
import com.walmart.aex.sp.dto.commitmentreport.InitialBumpSetResponse;
import com.walmart.aex.sp.dto.commitmentreport.Metrics;
import com.walmart.aex.sp.dto.commitmentreport.PackDetails;
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponse;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetStyle;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetPlanMapper {

	public void mapInitialSetPlan(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse,
								  InitialBumpSetResponse response, Integer fineline, StoreClusterMap storeClusterMap) {
		response.setFinelineNbr(fineline);
		response.setIntialSetStyles(mapInitialSetStyle(rfaInitialSetBumpSetResponse, response, storeClusterMap));

		response.getIntialSetStyles().removeIf(initialSetStyle -> initialSetStyle.getInitialSetPlan().isEmpty());

	}

	private List<InitialSetStyle> mapInitialSetStyle(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse,
													 InitialBumpSetResponse response, StoreClusterMap storeClusterMap) {
		List<InitialSetStyle> styles = Optional.ofNullable(response.getIntialSetStyles()).orElse(new ArrayList<>());

		styles.stream().filter(styleObj -> rfaInitialSetBumpSetResponse.getStyle_id().equals(styleObj.getStyleId())).findFirst()
				.ifPresentOrElse(styleObj -> styleObj
								.setInitialSetPlan(mapInitialSet(rfaInitialSetBumpSetResponse, styleObj,
										storeClusterMap)),
						() -> setInitialSetStyle(rfaInitialSetBumpSetResponse, styles, storeClusterMap));
		return styles;
	}

	private void setInitialSetStyle(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse,
									List<InitialSetStyle> styles, StoreClusterMap storeClusterMap) {
		InitialSetStyle style = new InitialSetStyle();
		style.setStyleId(rfaInitialSetBumpSetResponse.getStyle_id());
		style.setInitialSetPlan(mapInitialSet(rfaInitialSetBumpSetResponse, style, storeClusterMap));
		styles.add(style);
	}

	private List<InitialSetPlan> mapInitialSet(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse,
											   InitialSetStyle style, StoreClusterMap storeClusterMap) {
		List<InitialSetPlan> initialSetPlans = Optional.ofNullable(style.getInitialSetPlan()).orElse(new ArrayList<>());

		if (StringUtils.isNotEmpty(rfaInitialSetBumpSetResponse.getIn_store_week()))
			initialSetPlans.stream()
					.filter(initialSetPlan -> rfaInitialSetBumpSetResponse.getIn_store_week().equals(initialSetPlan.getInStoreWeek()))
					.findFirst()
					.ifPresentOrElse(initialSetPlan -> initialSetPlan
									.setPackDetails(mapInitialSetPack(rfaInitialSetBumpSetResponse, initialSetPlan,
											storeClusterMap)),
							() -> setInitialSetPlan(rfaInitialSetBumpSetResponse, initialSetPlans, storeClusterMap));
		return initialSetPlans;
	}

	private void setInitialSetPlan(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse,
								   List<InitialSetPlan> initialSetPlans, StoreClusterMap storeClusterMap) {
		InitialSetPlan initialSetPlan = new InitialSetPlan();
		initialSetPlan.setInStoreWeek(rfaInitialSetBumpSetResponse.getIn_store_week());
		initialSetPlan.setPackDetails(mapInitialSetPack(rfaInitialSetBumpSetResponse, initialSetPlan, storeClusterMap));
		initialSetPlans.add(initialSetPlan);
	}

	private List<PackDetails> mapInitialSetPack(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse,
												InitialSetPlan initialSetPlan, StoreClusterMap storeClusterMap) {
		List<PackDetails> packDetails2 = Optional.ofNullable(initialSetPlan.getPackDetails()).orElse(new ArrayList<>());

		packDetails2.stream().filter(packDetails -> rfaInitialSetBumpSetResponse.getPack_id().equals(packDetails.getPackId())).findFirst()
				.ifPresentOrElse(packDetails -> packDetails.setMetrics(mapInitialSetPackMetrics(rfaInitialSetBumpSetResponse, packDetails)),
						() -> setInitialSetPack(rfaInitialSetBumpSetResponse, packDetails2, storeClusterMap));
		return packDetails2;
	}

	private void setInitialSetPack(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse,
								   List<PackDetails> packDetails, StoreClusterMap storeClusterMap) {
		PackDetails paDetails = new PackDetails();
		paDetails.setPackId(rfaInitialSetBumpSetResponse.getPack_id());
		Optional.ofNullable(storeClusterMap)
				.map(storeCluster -> storeCluster.getKey(rfaInitialSetBumpSetResponse.getStore()))
				.filter(StringUtils::isNotEmpty)
				.ifPresent(groupingType -> paDetails.getGroupingTypes().add(groupingType));
		paDetails.setMetrics(mapInitialSetPackMetrics(rfaInitialSetBumpSetResponse,paDetails));
		paDetails.setUuId(rfaInitialSetBumpSetResponse.getUuid());
		paDetails.setBumpPackNbr(rfaInitialSetBumpSetResponse.getBumpPackNbr());
		packDetails.add(paDetails);
	}

	private List<Metrics> mapInitialSetPackMetrics(RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse, PackDetails packDetails) {
		List<Metrics> metrics = Optional.ofNullable(packDetails.getMetrics()).orElse(new ArrayList<>());
		Metrics metrics2 = new Metrics();
		metrics2.setSize(rfaInitialSetBumpSetResponse.getSize());
		//if no IS this would be a bump set
		if(rfaInitialSetBumpSetResponse.getInitialpack_ratio() !=null && rfaInitialSetBumpSetResponse.getInitialpack_ratio()>0) {
			metrics2.setRatio(rfaInitialSetBumpSetResponse.getInitialpack_ratio());
			metrics2.setQuantity(rfaInitialSetBumpSetResponse.getIs_quantity());

		}else {
			metrics2.setRatio(rfaInitialSetBumpSetResponse.getBumppack_ratio());
			metrics2.setQuantity(rfaInitialSetBumpSetResponse.getBs_quantity());
		}
		metrics2.setCcId(rfaInitialSetBumpSetResponse.getCc());
		metrics2.setMerchMethod(rfaInitialSetBumpSetResponse.getMerch_method());
		metrics.add(metrics2);
		return metrics;
	}
}
