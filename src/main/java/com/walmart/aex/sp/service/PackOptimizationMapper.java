package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.BuyQuantitiesDto;
import com.walmart.aex.sp.dto.packoptimization.CcPackDto;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackDto;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.FixtureDto;
import com.walmart.aex.sp.dto.packoptimization.MetricsPackDto;
import com.walmart.aex.sp.dto.packoptimization.SizePackDto;
import com.walmart.aex.sp.dto.packoptimization.StoreObjectDto;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PackOptimizationMapper {
	private static final ObjectMapper objectMapper = new ObjectMapper();


	public void mapPackOptimizationFineline(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FineLinePackOptimizationResponse response) {
		if (response.getPlanId() == null) {
			response.setPlanId(finelinePackOptimizationResponseDTO.getPlanId());
		}

		if (response.getPlanDesc() == null) {
			response.setPlanDesc(finelinePackOptimizationResponseDTO.getPlanDesc());
		}

		response.setFinelines(mapReplenishmentFl(finelinePackOptimizationResponseDTO, response));
	}

	private List<FineLinePackDto> mapReplenishmentFl(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FineLinePackOptimizationResponse response) {
		List<FineLinePackDto> finelineDtoList = Optional.ofNullable(response.getFinelines()).orElse(new ArrayList<>());

		finelineDtoList.stream()
		.filter(finelineDto -> finelinePackOptimizationResponseDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
		.ifPresentOrElse(finelineDto -> finelineDto.setCustomerChoices(mapReplenishmentCc(finelinePackOptimizationResponseDTO, finelineDto)),
				() -> setFinelineSP(finelinePackOptimizationResponseDTO, finelineDtoList));
		return finelineDtoList;
	}

	private void setFinelineSP(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<FineLinePackDto> finelineDtoList) {
		FineLinePackDto fineline = new FineLinePackDto();
		fineline.setFinelineNbr(finelinePackOptimizationResponseDTO.getFinelineNbr());
		fineline.setCustomerChoices(mapReplenishmentCc(finelinePackOptimizationResponseDTO, fineline));
		finelineDtoList.add(fineline);
	}


	private List<CcPackDto> mapReplenishmentCc(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FineLinePackDto finelineDto) {
		List<CcPackDto> customerChoiceList = Optional.ofNullable(finelineDto.getCustomerChoices()).orElse(new ArrayList<>());

		customerChoiceList.stream()
		.filter(customerChoiceDto -> finelinePackOptimizationResponseDTO.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
		.ifPresentOrElse(customerChoiceDto -> customerChoiceDto.setFixtures(mapMerchMethod(finelinePackOptimizationResponseDTO, customerChoiceDto)),
				() -> setCcSP(finelinePackOptimizationResponseDTO, customerChoiceList));
		return customerChoiceList;

	}

	private void setCcSP(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<CcPackDto> customerChoiceDtoList) {

		CcPackDto customerChoiceDto = new CcPackDto();
		customerChoiceDto.setCcId(finelinePackOptimizationResponseDTO.getCcId());
		customerChoiceDto.setFixtures(mapMerchMethod(finelinePackOptimizationResponseDTO, customerChoiceDto));
		customerChoiceDtoList.add(customerChoiceDto);
	}

	private List<FixtureDto> mapMerchMethod(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, CcPackDto customerChoiceDto) {
		List<FixtureDto> merchMethodsDtoList = Optional.ofNullable(customerChoiceDto.getFixtures()).orElse(new ArrayList<>());

		merchMethodsDtoList.stream()
		.filter(FixtureDto -> finelinePackOptimizationResponseDTO.getFixtureTypeRollupName().equals(FixtureDto.getFixtureType())).findFirst()
		.ifPresentOrElse(FixtureDto -> FixtureDto.setSizes(mapSize(finelinePackOptimizationResponseDTO, FixtureDto)),
				() -> setMerch(finelinePackOptimizationResponseDTO, merchMethodsDtoList));
		return merchMethodsDtoList;

	}

	private void setMerch(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<FixtureDto> merchMethodsDtoList) {

		FixtureDto fixtureDto = new FixtureDto();
		fixtureDto.setFixtureType(finelinePackOptimizationResponseDTO.getFixtureTypeRollupName());
		fixtureDto.setMerchMethod(finelinePackOptimizationResponseDTO.getMerchMethod());
		fixtureDto.setFlowStrategyType(finelinePackOptimizationResponseDTO.getFpStrategyText());
		fixtureDto.setSizes(mapSize(finelinePackOptimizationResponseDTO, fixtureDto));
		merchMethodsDtoList.add(fixtureDto);

	}


	private List<SizePackDto> mapSize(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, FixtureDto fixtureDto) {
		List<SizePackDto> sizeDtoList = Optional.ofNullable(fixtureDto.getSizes()).orElse(new ArrayList<>());

		sizeDtoList.stream()
		.filter(SizePackDto -> finelinePackOptimizationResponseDTO.getAhsSizeDesc().equals(SizePackDto.getSizeDesc())).findFirst()
		.ifPresentOrElse(customerChoiceDto ->log.info("Size implementation"),
				()->setSizes(finelinePackOptimizationResponseDTO, sizeDtoList));
		return sizeDtoList;
	}


	private void setSizes(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, List<SizePackDto> sizeDtoList) {

		SizePackDto sizeDto= new SizePackDto();
		sizeDto.setSizeDesc(finelinePackOptimizationResponseDTO.getAhsSizeDesc());
		sizeDto.setMetrics(mapMetrics(finelinePackOptimizationResponseDTO, sizeDto));
		sizeDtoList.add(sizeDto);

	}


	private List<MetricsPackDto> mapMetrics(FineLinePackOptimizationResponseDTO finelinePackOptimizationResponseDTO, SizePackDto sizeDto) {
		List<MetricsPackDto> metricsDtoList = Optional.ofNullable(sizeDto.getMetrics()).orElse(new CopyOnWriteArrayList<>());

		String storeDetails=finelinePackOptimizationResponseDTO.getStoreObj();
		List<BuyQuantitiesDto> storeObjectList=new ArrayList<>();

		StoreObjectDto storeObj = new StoreObjectDto() ;
		MetricsPackDto metricsDto = new MetricsPackDto();
		try {
			if(storeDetails!=null) {
				storeObj = (objectMapper.readValue(storeDetails, StoreObjectDto.class));
				storeObjectList=storeObj.getBuyQuantities();
			}

			for(BuyQuantitiesDto stObj: storeObjectList)
			{
				if(metricsDtoList.size() == 0)
				{
					metricsDto.setClusterId(stObj.getSizeCluster());
					metricsDto.setStoreList(stObj.getStoreList());
					metricsDto.setInitialSet(stObj.getIsUnits());
					if(stObj.getBumpSets().size()!=0) {
						metricsDto.setBumpSet(stObj.getBumpSets().get(0).getBsUnits());	
					}
					metricsDtoList.add(metricsDto);

				}
				else
				{ 
					metricsDtoList.stream()
					.filter(metrics -> ((metrics.getClusterId().equals(stObj.getSizeCluster())) && (metrics.getInitialSet().equals(stObj.getIsUnits())) )).findFirst()
					.ifPresentOrElse(metrics ->metrics.getStoreList().addAll(stObj.getStoreList()),
							()->setMetrics(metricsDtoList,stObj));
				}	
			}
		} catch (JsonMappingException e) {
			log.error("Error while parsing the Json",e.getMessage());
		} catch (JsonProcessingException e) {
			log.error("Error while parsing the Json",e.getMessage());
		}


		return metricsDtoList; 
	}

	private void setMetrics(List<MetricsPackDto> metricsDtoList,BuyQuantitiesDto stObj) {

		MetricsPackDto metricsObj = new MetricsPackDto();
		metricsObj.setClusterId(stObj.getSizeCluster());
		metricsObj.setStoreList(stObj.getStoreList());
		metricsObj.setInitialSet(stObj.getIsUnits());
		if(stObj.getBumpSets().size()!=0) {
			metricsObj.setBumpSet(stObj.getBumpSets().get(0).getBsUnits()); 
		}
		metricsDtoList.add(metricsObj); 



	}




}