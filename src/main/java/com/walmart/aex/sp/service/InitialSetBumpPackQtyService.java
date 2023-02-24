package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitSetBumpPackDTO;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitSetBumpPackData;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitialSetBumpPackQtyData;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class InitialSetBumpPackQtyService {

	private final BigQueryInitSetBpPkQtyService bigQueryInitSetBpPkQtyService;
	private final InitialSetBumpPackQtyMapper initSetBpPkQtyMapper;
	private final SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;

	public InitialSetBumpPackQtyService(BigQueryInitSetBpPkQtyService bigQueryInitSetBpPkQtyService,
										InitialSetBumpPackQtyMapper initSetBpPkQtyMapper, SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository) {
		this.bigQueryInitSetBpPkQtyService = bigQueryInitSetBpPkQtyService;
		this.initSetBpPkQtyMapper = initSetBpPkQtyMapper;
		this.spCustomerChoiceChannelFixtureSizeRepository = spCustomerChoiceChannelFixtureSizeRepository;
	}

	public BuyQtyResponse getInitSetBpPkByPlanFineline(BuyQtyRequest request) {
		BuyQtyResponse response = new BuyQtyResponse();
		InitSetBumpPackData initSetBpPkData = new InitSetBumpPackData();
		List<InitialSetBumpPackQtyData> initSetBpPkQtyDataList = new ArrayList<>();

		try {
			if (request.getPlanId() != null && request.getChannel().equalsIgnoreCase(ChannelType.STORE.name())
					&& request.getFinelineNbr() != null) {
				initSetBpPkData = bigQueryInitSetBpPkQtyService.fetchInitialSetBumpPackDataFromGCP(request);
			}
			Map<String, Integer> ahsSizeDescMap = getAhsSizeDescMap(request.getPlanId(), request.getFinelineNbr());
			if (initSetBpPkData != null && initSetBpPkData.getInitSetBpPkQtyDTOList() != null) {
				mapInitSetBumpPackQty(initSetBpPkData.getInitSetBpPkQtyDTOList(), initSetBpPkQtyDataList, ahsSizeDescMap);
			}

			Optional.of(initSetBpPkQtyDataList).stream().flatMap(Collection::stream).forEach(
					initSetBpPkQtyData -> initSetBpPkQtyMapper.mapInitSetBpPkQtyLvl2Sp(initSetBpPkQtyData, response));
		} catch (Exception e) {
			log.error("Exception While fetching CC Initial Set Qunatities :", e);
		}

		return response;
	}

	private Map<String, Integer> getAhsSizeDescMap(Long planId, Integer fineLineNbr) {
		Map<String, Integer> ahsSizeDescMap = new HashMap<>();
		List<SpCustomerChoiceChannelFixtureSize> spCcChanFixtrDataByPlanFineline = spCustomerChoiceChannelFixtureSizeRepository.getSpCcChanFixtrDataByPlanFineline(planId, fineLineNbr);
		spCcChanFixtrDataByPlanFineline.forEach(spCustomerChoiceChannelFixtureSize -> ahsSizeDescMap.putIfAbsent(spCustomerChoiceChannelFixtureSize.getAhsSizeDesc(), spCustomerChoiceChannelFixtureSize.getSpCustomerChoiceChannelFixtureSizeId().getAhsSizeId()));
		return ahsSizeDescMap;
	}

	private void mapInitSetBumpPackQty(List<InitSetBumpPackDTO> gcpInitSetBpPkQtyDataList,
			List<InitialSetBumpPackQtyData> initSetBpPkQtyDataList, Map<String, Integer> ahsSizeDescMap) {
		gcpInitSetBpPkQtyDataList.forEach(gcpInitSetBpPkQtyObj -> {
			String planIdAndFineline = gcpInitSetBpPkQtyObj.getPlanAndFineline();
			String[] planFineline = planIdAndFineline.split("[_-]");

			InitialSetBumpPackQtyData initSetBpPkQtyData = new InitialSetBumpPackQtyData();
			initSetBpPkQtyData.setPlanId(Long.parseLong(planFineline[0]));
			initSetBpPkQtyData.setLvl0Nbr(0);// Currently there are no lvl0 lvl1 lvl2 lvl3 lvl4 in the GCP table
			initSetBpPkQtyData.setLvl1Nbr(0);// Hence setting them as 0 for now
			initSetBpPkQtyData.setLvl2Nbr(0);
			initSetBpPkQtyData.setLvl3Nbr(0);
			initSetBpPkQtyData.setLvl4Nbr(0);
			initSetBpPkQtyData.setFinelineNbr(Integer.parseInt(planFineline[1]));
			initSetBpPkQtyData.setStyleNbr(gcpInitSetBpPkQtyObj.getStyleNbr());
			initSetBpPkQtyData.setCcId(gcpInitSetBpPkQtyObj.getCustomerChoice());
			initSetBpPkQtyData.setMerchMethodDesc(gcpInitSetBpPkQtyObj.getMerchMethodDesc());
			initSetBpPkQtyData.setSizeDesc(gcpInitSetBpPkQtyObj.getSize());
			initSetBpPkQtyData.setFinalInitialSetQty(gcpInitSetBpPkQtyObj.getFinalInitialSetQty());
			initSetBpPkQtyData.setBumpPackQty(gcpInitSetBpPkQtyObj.getBumpPackQty());
			initSetBpPkQtyData.setAhsSizeId(ahsSizeDescMap.get(gcpInitSetBpPkQtyObj.getSize()));

			initSetBpPkQtyDataList.add(initSetBpPkQtyData);
		});

	}
}
