package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.enums.FlowStrategy;
import com.walmart.aex.sp.util.CommonUtil;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PackOptimizationMapper {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Integer DEFAULT_BUMPPACK = 1;


	public void mapPackOptimizationFineline(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FineLinePackOptimizationResponse response,Long planId, Integer bumpPackNbr ) {
		response.setPlanId(planId);
		response.setFinelines(mapReplenishmentFl(finelinePackOptimizationResponseDTO, response, bumpPackNbr));
	}

	private List<FineLinePackDto> mapReplenishmentFl(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FineLinePackOptimizationResponse response, Integer bumpPackNbr) {
		List<FineLinePackDto> finelineDtoList = Optional.ofNullable(response.getFinelines()).orElse(new ArrayList<>());

		finelineDtoList.stream()
		.filter(finelineDto -> finelinePackOptimizationResponseDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
		.ifPresentOrElse(finelineDto -> finelineDto.setCustomerChoices(mapReplenishmentCc(finelinePackOptimizationResponseDTO, finelineDto, bumpPackNbr)),
				() -> setFinelineSP(finelinePackOptimizationResponseDTO, finelineDtoList, bumpPackNbr));
		return finelineDtoList;
	}

	private void setFinelineSP(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<FineLinePackDto> finelineDtoList, Integer bumpPackNbr) {
		FineLinePackDto fineline = new FineLinePackDto();
		fineline.setFinelineNbr(finelinePackOptimizationResponseDTO.getFinelineNbr());
		fineline.setFinelineLevelConstraints(setFinelineConstraints(finelinePackOptimizationResponseDTO,fineline));
		fineline.setCustomerChoices(mapReplenishmentCc(finelinePackOptimizationResponseDTO, fineline, bumpPackNbr));
		finelineDtoList.add(fineline);
	}
	
	private List<CcPackDto> mapReplenishmentCc(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FineLinePackDto finelineDto, Integer bumpPackNbr) {
		List<CcPackDto> customerChoiceList = Optional.ofNullable(finelineDto.getCustomerChoices()).orElse(new ArrayList<>());

		customerChoiceList.stream()
		.filter(customerChoiceDto -> finelinePackOptimizationResponseDTO.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
		.ifPresentOrElse(customerChoiceDto -> customerChoiceDto.setFixtures(mapMerchMethod(finelinePackOptimizationResponseDTO, customerChoiceDto, bumpPackNbr)),
				() -> setCcSP(finelinePackOptimizationResponseDTO, customerChoiceList, bumpPackNbr));
		return customerChoiceList;

	}

	private void setCcSP(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<CcPackDto> customerChoiceDtoList, Integer bumpPackNbr) {

		CcPackDto customerChoiceDto = new CcPackDto();
		customerChoiceDto.setCcId(finelinePackOptimizationResponseDTO.getCcId());
		customerChoiceDto.setFixtures(mapMerchMethod(finelinePackOptimizationResponseDTO, customerChoiceDto, bumpPackNbr));
		customerChoiceDto.setColorCombinationConstraints(setColorConstraints(finelinePackOptimizationResponseDTO,customerChoiceDto));
		customerChoiceDtoList.add(customerChoiceDto);
	}

	private List<FixtureDto> mapMerchMethod(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, CcPackDto customerChoiceDto, Integer bumpPackNbr) {
		List<FixtureDto> merchMethodsDtoList = Optional.ofNullable(customerChoiceDto.getFixtures()).orElse(new ArrayList<>());

		merchMethodsDtoList.stream()
		.filter(FixtureDto -> finelinePackOptimizationResponseDTO.getFixtureTypeRollupName().equals(FixtureDto.getFixtureType())).findFirst()
		.ifPresentOrElse(FixtureDto -> FixtureDto.setSizes(mapSize(finelinePackOptimizationResponseDTO, FixtureDto, bumpPackNbr)),
				() -> setMerch(finelinePackOptimizationResponseDTO, merchMethodsDtoList, bumpPackNbr));
		return merchMethodsDtoList;

	}

	private void setMerch(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<FixtureDto> merchMethodsDtoList, Integer bumpPackNbr) {

		FixtureDto fixtureDto = new FixtureDto();
		fixtureDto.setFixtureType(finelinePackOptimizationResponseDTO.getFixtureTypeRollupName());
		fixtureDto.setMerchMethod(CommonUtil.getMerchMethod( finelinePackOptimizationResponseDTO.getMerchMethod()));
		fixtureDto.setSizes(mapSize(finelinePackOptimizationResponseDTO, fixtureDto, bumpPackNbr));
		merchMethodsDtoList.add(fixtureDto);

	}


	private List<SizePackDto> mapSize(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FixtureDto fixtureDto, Integer bumpPackNbr) {
		List<SizePackDto> sizeDtoList = Optional.ofNullable(fixtureDto.getSizes()).orElse(new ArrayList<>());

		sizeDtoList.stream()
		.filter(SizePackDto -> finelinePackOptimizationResponseDTO.getAhsSizeDesc().equals(SizePackDto.getSizeDesc())).findFirst()
		.ifPresentOrElse(customerChoiceDto ->log.info("Size implementation"),
				()->setSizes(finelinePackOptimizationResponseDTO, sizeDtoList, bumpPackNbr));
		return sizeDtoList;
	}


	private void setSizes(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<SizePackDto> sizeDtoList, Integer bumpPackNbr) {

		SizePackDto sizeDto= new SizePackDto();
		sizeDto.setSizeDesc(finelinePackOptimizationResponseDTO.getAhsSizeDesc());
		sizeDto.setMetrics(mapMetrics(finelinePackOptimizationResponseDTO, sizeDto, bumpPackNbr));
		sizeDtoList.add(sizeDto);

	}


	private List<MetricsPackDto> mapMetrics(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, SizePackDto sizeDto, Integer bumpPackNbr) {
		List<MetricsPackDto> metricsDtoList = Optional.ofNullable(sizeDto.getMetrics()).orElse(new CopyOnWriteArrayList<>());

		String storeDetails=finelinePackOptimizationResponseDTO.getStoreObj();
		List<BuyQuantitiesDto> storeObjectList=new ArrayList<>();

		try {
			if(storeDetails!=null) {
				StoreObjectDto storeObj = (objectMapper.readValue(storeDetails, StoreObjectDto.class));
				storeObjectList=storeObj.getBuyQuantities();
			}

			for(BuyQuantitiesDto stObj: storeObjectList)
			{
				setMetrics(metricsDtoList,stObj,bumpPackNbr);
			}
		} catch (JsonProcessingException e) {
			log.error("Error while parsing the Json: {}",e.getMessage());
		}

		return metricsDtoList; 
	}

	private void setMetrics(List<MetricsPackDto> metricsDtoList,BuyQuantitiesDto stObj, Integer bumpPackNbr) {

		MetricsPackDto metricsObj = new MetricsPackDto();
		metricsObj.setClusterId(stObj.getSizeCluster());
		metricsObj.setStoreList(stObj.getStoreList());
		metricsObj.setBumpSet(0);
		if (null!=bumpPackNbr && bumpPackNbr>1) {
			metricsObj.setInitialSet(0);
		}
		else {
			metricsObj.setInitialSet(stObj.getIsUnits());
		}
		metricsObj.setFlowStrategyType(FlowStrategy.getFlowStrategyFromId(stObj.getFlowStrategyCode()));
		if(null!=bumpPackNbr && stObj.getBumpSets().size()>=bumpPackNbr) {
			BumpSetDto bumpSetDto = getBumpPackByNbr(stObj, bumpPackNbr);
			if(null!=bumpSetDto) {
				metricsObj.setBumpSet(bumpSetDto.getBsUnits());
			}
		}
		if (metricsObj.getInitialSet() > 0 || metricsObj.getBumpSet() > 0) {
			metricsDtoList.add(metricsObj);
		}
	}

	private BumpSetDto getBumpPackByNbr(BuyQuantitiesDto stObj, Integer bumpPackNbr) {
		return Optional.ofNullable(stObj)
				.map(BuyQuantitiesDto::getBumpSets)
				.stream()
				.flatMap(Collection::stream)
				.filter(bumpSetDto -> (bumpPackNbr.equals(DEFAULT_BUMPPACK) && null==bumpSetDto.getSetNbr()) || bumpPackNbr.equals(bumpSetDto.getSetNbr()))
				.findFirst()
				.orElse(null);
	}


	private FinelineLevelConstraints setFinelineConstraints(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FineLinePackDto fineline) {
		
		FinelineLevelConstraints  finelineConstraints = new FinelineLevelConstraints();
		finelineConstraints.setMaxUnitsPerPack(finelinePackOptimizationResponseDTO.getMaxUnitsPerPack());
		finelineConstraints.setMaxPacks(finelinePackOptimizationResponseDTO.getMaxNbrOfPacks());
		fineline.setFinelineLevelConstraints(finelineConstraints);
		return finelineConstraints;
	}


	private ColorCombinationConstraints setColorConstraints(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, CcPackDto customerChoiceDto) {
		
		ColorCombinationConstraints colorConstraints = new  ColorCombinationConstraints();
		colorConstraints.setFactoryId(finelinePackOptimizationResponseDTO.getFactoryId()); 
		colorConstraints.setColorCombination(finelinePackOptimizationResponseDTO.getColorCombination());
		colorConstraints.setSinglePackIndicator(finelinePackOptimizationResponseDTO.getSinglePackInd());
		customerChoiceDto.setColorCombinationConstraints(colorConstraints);
		return colorConstraints;
	}
	
	

	



}