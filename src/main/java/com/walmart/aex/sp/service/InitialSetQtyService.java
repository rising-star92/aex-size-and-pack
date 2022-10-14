package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.initialsetqty.InitialSetQtyData;
import com.walmart.aex.sp.dto.initialsetqty.RFAInitialSetDTO;
import com.walmart.aex.sp.dto.initialsetqty.RFAInitialSetData;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.enums.ChannelType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetQtyService {

	private final BigQueryInitialSetQtyService bigQueryInitialSetQtyService;
	private final InitialSetQtyMapper initialSetQtyMapper;

	public InitialSetQtyService(BigQueryInitialSetQtyService bigQueryInitialSetQtyService,
			InitialSetQtyMapper initialSetQtyMapper) {
		this.bigQueryInitialSetQtyService = bigQueryInitialSetQtyService;
		this.initialSetQtyMapper = initialSetQtyMapper;
	}

	public BuyQtyResponse getInitialSetByPlanFineline(BuyQtyRequest request) {
		BuyQtyResponse response = new BuyQtyResponse();
		RFAInitialSetData rfaInitialSetData = new RFAInitialSetData();
		List<InitialSetQtyData> intialSetQtyDataList = new ArrayList<>();

		try {
			if (request.getPlanId() != null && request.getChannel().equalsIgnoreCase(ChannelType.STORE.name())
					&& request.getFinelineNbr() != null) {
				rfaInitialSetData = bigQueryInitialSetQtyService.fetchInitialSetDataFromGCP(request);
			}

			if (rfaInitialSetData != null && rfaInitialSetData.getRfaInitialSetQtyData() != null) {
				mapInitialSetQty(rfaInitialSetData.getRfaInitialSetQtyData(), intialSetQtyDataList);
			}

			Optional.of(intialSetQtyDataList).stream().flatMap(Collection::stream).forEach(
					intialSetQtyData -> initialSetQtyMapper.mapInitialSetQtyLvl2Sp(intialSetQtyData, response));
		} catch (Exception e) {
			log.error("Exception While fetching CC Initial Set Qunatities :", e);
		}

		return response;
	}

	private void mapInitialSetQty(List<RFAInitialSetDTO> rfaInitialSetQtyData,
			List<InitialSetQtyData> intialSetQtyDataList) {
		rfaInitialSetQtyData.forEach(rfaInitialSetQtyObj -> {
			String planIdAndFineline = rfaInitialSetQtyObj.getPlanAndFineline();
			String[] planFineline = planIdAndFineline.split("_");

			InitialSetQtyData initialSetQtyData = new InitialSetQtyData();
			initialSetQtyData.setPlanId(Long.parseLong(planFineline[0]));
			initialSetQtyData.setLvl0Nbr(0);// Currently there are no lvl0 lvl1 lvl2 lvl3 lvl4 in the GCP table
			initialSetQtyData.setLvl1Nbr(0);// Hence setting them as 0 for now
			initialSetQtyData.setLvl2Nbr(0);
			initialSetQtyData.setLvl3Nbr(0);
			initialSetQtyData.setLvl4Nbr(0);
			initialSetQtyData.setFinelineNbr(Integer.parseInt(planFineline[1]));
			initialSetQtyData.setStyleNbr(rfaInitialSetQtyObj.getStyleNbr());
			initialSetQtyData.setCcId(rfaInitialSetQtyObj.getCustomerChoice());
			initialSetQtyData.setMerchMethodDesc(rfaInitialSetQtyObj.getMerchMethodDesc());
			initialSetQtyData.setSizeDesc(rfaInitialSetQtyObj.getSize());
			initialSetQtyData.setFinalInitialSetQty(rfaInitialSetQtyObj.getFinalInitialSetQty());

			intialSetQtyDataList.add(initialSetQtyData);

		});

	}
}
