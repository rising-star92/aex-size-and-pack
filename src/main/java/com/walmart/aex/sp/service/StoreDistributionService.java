package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.walmart.aex.sp.dto.StoreClusterMap;
import com.walmart.aex.sp.exception.SizeAndPackException;
import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.storedistribution.FinelineData;
import com.walmart.aex.sp.dto.storedistribution.PackData;
import com.walmart.aex.sp.dto.storedistribution.PackInfo;
import com.walmart.aex.sp.dto.storedistribution.PackInfoRequest;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionDTO;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionData;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionResponse;
import com.walmart.aex.sp.enums.ChannelType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StoreDistributionService {

	private final BigQueryStoreDistributionService bigQueryStoreDistributionService;
	private final StoreDistributionMapper storeDistributionMapper;
	private final StoreClusterService storeClusterService;

	public StoreDistributionService(BigQueryStoreDistributionService bigQueryStoreDistributionService,
									StoreDistributionMapper storeDistributionMapper, StoreClusterService storeClusterService) {
		this.bigQueryStoreDistributionService = bigQueryStoreDistributionService;
		this.storeDistributionMapper = storeDistributionMapper;
		this.storeClusterService = storeClusterService;
	}

	public StoreDistributionResponse fetchStoreDistributionResponse(PackInfoRequest request) {
		StoreDistributionResponse response = new StoreDistributionResponse();
		List<StoreDistributionDTO> storeDistributionList = new ArrayList<>();
		List<PackInfo> packInfoList = new ArrayList<>();

		try {
			if (request != null && request.getPackInfoList() != null)
				packInfoList = request.getPackInfoList();

			storeDistributionList = callBigQueryService(packInfoList);

			Optional.of(storeDistributionList).stream().flatMap(Collection::stream)
					.forEach(storeDistributionDto -> storeDistributionMapper
							.mapStoreDistributionResponse(storeDistributionDto, response));
		} catch (Exception e) {
			log.error("Exception While fetching Store Distribution :", e);
		}

		return response;
	}

	private List<StoreDistributionDTO> callBigQueryService(List<PackInfo> packInfoList) {
		List<StoreDistributionDTO> storeDistributionList = new ArrayList<>();
		PackData packData = new PackData();

		try {
			packInfoList.forEach(packInfoObj -> {
				if (packInfoObj.getPlanId() != null
						&& packInfoObj.getChannel().equalsIgnoreCase(ChannelType.STORE.name())
						&& packInfoObj.getFinelineDataList() != null) {

					packData.setPlanId(packInfoObj.getPlanId());
					List<FinelineData> finelineDataList = packInfoObj.getFinelineDataList();

					finelineDataList.forEach(fineline -> {
						if (fineline.getFinelineNbr() != null && fineline.getPackId() != null && fineline.getInStoreWeek() != null) {
							packData.setFinelineNbr(fineline.getFinelineNbr());
							packData.setPackId(fineline.getPackId());
							packData.setInStoreWeek(fineline.getInStoreWeek());

							StoreClusterMap storeClusterMap = null;
							StoreDistributionData storeDistributionData = bigQueryStoreDistributionService.getStoreDistributionData(packData);

							StoreClusterMap storeClusterMap = null;
							if(fineline.getGroupingType() != null && packInfoObj.getSeason() != null && packInfoObj.getFiscalYear() != null) {
								try {
									storeClusterMap = storeClusterService.fetchPOStoreClusterGrouping(packInfoObj.getSeason(), packInfoObj.getFiscalYear());
								} catch (SizeAndPackException e) {
									throw new RuntimeException(e);
								}
							}

							if(storeClusterMap != null && !storeClusterMap.isEmpty()) {
								List<Integer> stores = storeClusterMap.get(fineline.getGroupingType());

								if (storeDistributionData != null
										&& storeDistributionData.getStoreDistributionList() != null) {
									for (StoreDistributionDTO storeDistributionDTO : storeDistributionData.getStoreDistributionList()) {
										if (stores != null && stores.contains(storeDistributionDTO.getStore())) {
											storeDistributionList.add(storeDistributionDTO);
										}
									}
								}
							} else {
								if (storeDistributionData != null
										&& storeDistributionData.getStoreDistributionList() != null) {
									storeDistributionList.addAll(storeDistributionData.getStoreDistributionList());
								}
							}
						}
					});
				}
			});
		} catch (Exception e) {
			log.error("Exception details are ", e);
		}
		return storeDistributionList;
	}
}