package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.storedistribution.DistributionMetric;
import com.walmart.aex.sp.dto.storedistribution.InitialSetPlanData;
import com.walmart.aex.sp.dto.storedistribution.PackDistribution;
import com.walmart.aex.sp.dto.storedistribution.StoreDistribution;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionDTO;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StoreDistributionMapper {

	public void mapStoreDistributionResponse(StoreDistributionDTO storeDistributionDto,
			StoreDistributionResponse response) {
		response.setStoreDistributions(mapStoreDistribution(storeDistributionDto, response));
	}

	private List<StoreDistribution> mapStoreDistribution(StoreDistributionDTO storeDistributionDto,
			StoreDistributionResponse response) {
		List<StoreDistribution> storeDistributions = Optional.ofNullable(response.getStoreDistributions())
				.orElse(new ArrayList<>());

		storeDistributions.stream()
				.filter(storeDistrObj -> storeDistributionDto.getFinelineNbr().equals(storeDistrObj.getFinelineNbr())
						&& storeDistributionDto.getStyleNbr().equals(storeDistrObj.getStyleNbr()))
				.findFirst().ifPresentOrElse(
						storeDistrObj -> storeDistrObj
								.setInitialSetPlanDataList(mapInitialSetData(storeDistributionDto, storeDistrObj)),
						() -> setStoreDistribution(storeDistributionDto, storeDistributions));

		return storeDistributions;
	}

	private void setStoreDistribution(StoreDistributionDTO storeDistributionDto,
			List<StoreDistribution> storeDistributions) {
		StoreDistribution storeDistrObj = new StoreDistribution();
		storeDistrObj.setFinelineNbr(storeDistributionDto.getFinelineNbr());
		storeDistrObj.setStyleNbr(storeDistributionDto.getStyleNbr());
		storeDistrObj.setInitialSetPlanDataList(mapInitialSetData(storeDistributionDto, storeDistrObj));
		storeDistributions.add(storeDistrObj);
	}

	private List<InitialSetPlanData> mapInitialSetData(StoreDistributionDTO storeDistributionDto,
			StoreDistribution storeDistrObj) {
		List<InitialSetPlanData> initialSetPlanDataList = Optional.ofNullable(storeDistrObj.getInitialSetPlanDataList())
				.orElse(new ArrayList<>());

		initialSetPlanDataList.stream().filter(
				initialSetPlanObj -> storeDistributionDto.getInStoreWeek().equals(initialSetPlanObj.getInStoreWeek()))
				.findFirst().ifPresentOrElse(
						initialSetPlanObj -> initialSetPlanObj
								.setPackDistributionList(mapPackDistribution(storeDistributionDto, initialSetPlanObj)),
						() -> setInitialSetPlanData(storeDistributionDto, initialSetPlanDataList));

		return initialSetPlanDataList;
	}

	private void setInitialSetPlanData(StoreDistributionDTO storeDistributionDto,
			List<InitialSetPlanData> initialSetPlanDataList) {
		InitialSetPlanData initialSetPlanObj = new InitialSetPlanData();
		initialSetPlanObj.setInStoreWeek(storeDistributionDto.getInStoreWeek());
		initialSetPlanObj.setPackDistributionList(mapPackDistribution(storeDistributionDto, initialSetPlanObj));
		initialSetPlanDataList.add(initialSetPlanObj);
	}

	private List<PackDistribution> mapPackDistribution(StoreDistributionDTO storeDistributionDto,
			InitialSetPlanData initialSetPlanObj) {
		List<PackDistribution> packDistributionList = Optional.ofNullable(initialSetPlanObj.getPackDistributionList())
				.orElse(new ArrayList<>());

		packDistributionList.stream()
				.filter(packDistrObj -> storeDistributionDto.getPackId().equals(packDistrObj.getPackId())).findFirst()
				.ifPresentOrElse(
						packDistrObj -> packDistrObj
								.setDistributionMetricList(mapDistributionMetrics(storeDistributionDto, packDistrObj)),
						() -> setPackDistribution(storeDistributionDto, packDistributionList));

		return packDistributionList;
	}

	private void setPackDistribution(StoreDistributionDTO storeDistributionDto,
			List<PackDistribution> packDistributionList) {
		PackDistribution packDistrObj = new PackDistribution();
		packDistrObj.setPackId(storeDistributionDto.getPackId());
		packDistrObj.setDistributionMetricList(mapDistributionMetrics(storeDistributionDto, packDistrObj));
		packDistributionList.add(packDistrObj);
	}

	private List<DistributionMetric> mapDistributionMetrics(StoreDistributionDTO storeDistributionDto,
			PackDistribution packDistrObj) {
		List<DistributionMetric> distributionMetricList = Optional.ofNullable(packDistrObj.getDistributionMetricList())
				.orElse(new ArrayList<>());

		if (distributionMetricList.stream().noneMatch(distribution -> distribution.getStore().equals(storeDistributionDto.getStore()) && distribution.getMultiplier().equals(storeDistributionDto.getPackMultiplier()))) {
			DistributionMetric distributionMetric = new DistributionMetric();
			distributionMetric.setStore(storeDistributionDto.getStore());
			distributionMetric.setMultiplier(storeDistributionDto.getPackMultiplier());
			distributionMetricList.add(distributionMetric);
		}

		return distributionMetricList;
	}
}
