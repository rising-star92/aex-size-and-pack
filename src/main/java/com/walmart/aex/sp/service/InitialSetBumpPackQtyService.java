package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitialSetBumpPackQtyData;
import com.walmart.aex.sp.dto.initsetbumppkqty.RFAInitialSetBumpPackDTO;
import com.walmart.aex.sp.dto.initsetbumppkqty.RFAInitialSetBumpPackData;
import com.walmart.aex.sp.enums.ChannelType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetBumpPackQtyService {

	private final BigQueryInitSetBpPkQtyService bigQueryInitSetBpPkQtyService;
	private final InitialSetBumpPackQtyMapper initSetBpPkQtyMapper;

	public InitialSetBumpPackQtyService(BigQueryInitSetBpPkQtyService bigQueryInitSetBpPkQtyService,
			InitialSetBumpPackQtyMapper initSetBpPkQtyMapper) {
		this.bigQueryInitSetBpPkQtyService = bigQueryInitSetBpPkQtyService;
		this.initSetBpPkQtyMapper = initSetBpPkQtyMapper;
	}

	public BuyQtyResponse getInitSetBpPkByPlanFineline(BuyQtyRequest request) {
		BuyQtyResponse response = new BuyQtyResponse();
		RFAInitialSetBumpPackData rfaInitSetBpPkData = new RFAInitialSetBumpPackData();
		List<InitialSetBumpPackQtyData> initSetBpPkQtyDataList = new ArrayList<>();

		try {
			if (request.getPlanId() != null && request.getChannel().equalsIgnoreCase(ChannelType.STORE.name())
					&& request.getFinelineNbr() != null) {
				rfaInitSetBpPkData = bigQueryInitSetBpPkQtyService.fetchInitialSetBumpPackDataFromGCP(request);
			}

			if (rfaInitSetBpPkData != null && rfaInitSetBpPkData.getRfaInitSetBpPkQtyDataList() != null) {
				mapInitSetBumpPackQty(rfaInitSetBpPkData.getRfaInitSetBpPkQtyDataList(), initSetBpPkQtyDataList);
			}

			Optional.of(initSetBpPkQtyDataList).stream().flatMap(Collection::stream).forEach(
					initSetBpPkQtyData -> initSetBpPkQtyMapper.mapInitSetBpPkQtyLvl2Sp(initSetBpPkQtyData, response));
		} catch (Exception e) {
			log.error("Exception While fetching CC Initial Set Qunatities :", e);
		}

		return response;
	}

	private void mapInitSetBumpPackQty(List<RFAInitialSetBumpPackDTO> rfaInitSetBpPkQtyDataList,
			List<InitialSetBumpPackQtyData> initSetBpPkQtyDataList) {
		rfaInitSetBpPkQtyDataList.forEach(rfaInitSetBpPkQtyObj -> {
			String planIdAndFineline = rfaInitSetBpPkQtyObj.getPlanAndFineline();
			String[] planFineline = planIdAndFineline.split("_");

			InitialSetBumpPackQtyData initSetBpPkQtyData = new InitialSetBumpPackQtyData();
			initSetBpPkQtyData.setPlanId(Long.parseLong(planFineline[0]));
			initSetBpPkQtyData.setLvl0Nbr(0);// Currently there are no lvl0 lvl1 lvl2 lvl3 lvl4 in the GCP table
			initSetBpPkQtyData.setLvl1Nbr(0);// Hence setting them as 0 for now
			initSetBpPkQtyData.setLvl2Nbr(0);
			initSetBpPkQtyData.setLvl3Nbr(0);
			initSetBpPkQtyData.setLvl4Nbr(0);
			initSetBpPkQtyData.setFinelineNbr(Integer.parseInt(planFineline[1]));
			initSetBpPkQtyData.setStyleNbr(rfaInitSetBpPkQtyObj.getStyleNbr());
			initSetBpPkQtyData.setCcId(rfaInitSetBpPkQtyObj.getCustomerChoice());
			initSetBpPkQtyData.setMerchMethodDesc(rfaInitSetBpPkQtyObj.getMerchMethodDesc());
			initSetBpPkQtyData.setSizeDesc(rfaInitSetBpPkQtyObj.getSize());
			initSetBpPkQtyData.setFinalInitialSetQty(rfaInitSetBpPkQtyObj.getFinalInitialSetQty());
			initSetBpPkQtyData.setBumpPackQty(rfaInitSetBpPkQtyObj.getBumpPackQty());

			initSetBpPkQtyDataList.add(initSetBpPkQtyData);

		});

	}
}
