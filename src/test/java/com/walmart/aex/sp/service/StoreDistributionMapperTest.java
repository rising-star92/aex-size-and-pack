package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;

import com.walmart.aex.sp.dto.storedistribution.DistributionMetric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.storedistribution.StoreDistributionDTO;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionResponse;

@ExtendWith(MockitoExtension.class)
class StoreDistributionMapperTest {

	@InjectMocks
	StoreDistributionMapper storeDistributionMapper;

	@Test
	void getStoreDistributionResponseTest() {
		StoreDistributionResponse response = new StoreDistributionResponse();

		List<StoreDistributionDTO> storeDistributionList = getStoreDistributionDTO();

		Optional.of(storeDistributionList).stream().flatMap(Collection::stream)
				.forEach(storeDistributionDto -> storeDistributionMapper
						.mapStoreDistributionResponse(storeDistributionDto, response));
		assertNotNull(response);

		Set<DistributionMetric> distributionMetricSet = response.getStoreDistributions().get(0).getInitialSetPlanDataList().get(0).getPackDistributionList()
				.get(0).getDistributionMetricList();
		DistributionMetric distributionMetric = distributionMetricSet.stream().findFirst().get();

		assertEquals(2, distributionMetricSet.size());
		assertEquals(1, distributionMetric.getStore());
		assertEquals(1, distributionMetric.getMultiplier());
	}

	private List<StoreDistributionDTO> getStoreDistributionDTO() {

		List<StoreDistributionDTO> storeDistributionList = new ArrayList<>();
		StoreDistributionDTO storeDistributionDTO1 = new StoreDistributionDTO();
		StoreDistributionDTO storeDistributionDTO2 = new StoreDistributionDTO();

		storeDistributionDTO1.setFinelineNbr(2702);
		storeDistributionDTO1.setStyleNbr("34_2702_2_22_2");
		storeDistributionDTO1.setInStoreWeek(202321L);
		storeDistributionDTO1.setPackId("SP_is12_2702_0_34_2702_2_22_2_BLACK SOOT_FOLDED_1");
		storeDistributionDTO1.setStore(1);
		storeDistributionDTO1.setPackMultiplier(1);

		storeDistributionDTO1.setFinelineNbr(2702);
		storeDistributionDTO1.setStyleNbr("34_2702_2_22_2");
		storeDistributionDTO1.setInStoreWeek(202321L);
		storeDistributionDTO1.setPackId("SP_is12_2702_0_34_2702_2_22_2_BLACK SOOT_FOLDED_1");
		storeDistributionDTO1.setStore(1);
		storeDistributionDTO1.setPackMultiplier(1);

		storeDistributionDTO2.setFinelineNbr(2702);
		storeDistributionDTO2.setStyleNbr("34_2702_2_22_2");
		storeDistributionDTO2.setInStoreWeek(202321L);
		storeDistributionDTO2.setPackId("SP_is12_2702_0_34_2702_2_22_2_BLACK SOOT_FOLDED_1");
		storeDistributionDTO2.setStore(2);
		storeDistributionDTO2.setPackMultiplier(1);

		storeDistributionList.add(storeDistributionDTO1);
		storeDistributionList.add(storeDistributionDTO2);

		return storeDistributionList;
	}

}
