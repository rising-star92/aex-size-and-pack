package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.util.CommonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class SizeLevelReplenishmentMapper {
	@Autowired
	BuyQuantityMapper buyQuantityMapper;

	@Autowired
	ReplenishmentMapper replenishmentMapper;

	@Autowired
	ObjectMapper objectMapper;

	public void mapReplenishmentLvl2Sp(ReplenishmentResponseDTO replenishmentResponseDTO,
			ReplenishmentResponse response, Integer finelineNbr) {
		   if (response.getPlanId() == null) {
	            response.setPlanId(replenishmentResponseDTO.getPlanId());
	        }
	        if (response.getLvl0Nbr() == null) {
	            response.setLvl0Nbr(replenishmentResponseDTO.getLvl0Nbr());
	            response.setLvl0Desc(replenishmentResponseDTO.getLvl0Desc());
	        }
	        if (response.getLvl1Nbr() == null) {
	            response.setLvl1Nbr(replenishmentResponseDTO.getLvl1Nbr());
	            response.setLvl1Desc(replenishmentResponseDTO.getLvl1Desc());
	        }
	        if (response.getLvl2Nbr() == null) {
	            response.setLvl2Nbr(replenishmentResponseDTO.getLvl2Nbr());
	            response.setLvl2Desc(replenishmentResponseDTO.getLvl2Desc());
	        }
		response.setLvl3List(mapReplenishmentLvl3Sp(replenishmentResponseDTO, response, finelineNbr));
	}

	private List<Lvl3Dto> mapReplenishmentLvl3Sp(ReplenishmentResponseDTO replenishmentResponseDTO,
			ReplenishmentResponse response, Integer finelineNbr) {
		List<Lvl3Dto> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

		lvl3List.stream().filter(lvl3 -> replenishmentResponseDTO.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
				.ifPresentOrElse(
						lvl3 -> lvl3.setLvl4List(mapReplenishmentLvl4Sp(replenishmentResponseDTO, lvl3, finelineNbr)),
						() -> setLvl3SP(replenishmentResponseDTO, lvl3List, finelineNbr));
		return lvl3List;
	}

	private void setLvl3SP(ReplenishmentResponseDTO replenishmentResponseDTO, List<Lvl3Dto> lvl3List,
			Integer finelineNbr) {
		Lvl3Dto lvl3 = new Lvl3Dto();
		lvl3.setLvl3Nbr(replenishmentResponseDTO.getLvl3Nbr());
		lvl3.setLvl3Desc(replenishmentResponseDTO.getLvl3Desc());
		if (finelineNbr == null) {
			List<Lvl4Dto> lvl4DtoList = mapReplenishmentLvl4Sp(replenishmentResponseDTO, lvl3, finelineNbr);
			MetricsDto metricsDto = buyQuantityMapper.lvl4MetricsAggregateQtys(lvl4DtoList);
			lvl3.setMetrics(metricsDto);
			lvl3.setLvl4List(lvl4DtoList);
		} else { 
			lvl3.setLvl4List(mapReplenishmentLvl4Sp(replenishmentResponseDTO, lvl3, finelineNbr));
		}
		lvl3List.add(lvl3);
	} 

	private List<Lvl4Dto> mapReplenishmentLvl4Sp(ReplenishmentResponseDTO replenishmentResponseDTO, Lvl3Dto lvl3,
			Integer finelineNbr) {
		List<Lvl4Dto> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

		lvl4DtoList.stream().filter(lvl4 -> replenishmentResponseDTO.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
				.ifPresentOrElse(
						lvl4 -> lvl4.setFinelines(mapReplenishmentFl(replenishmentResponseDTO, lvl4, finelineNbr)),
						() -> setLvl4SP(replenishmentResponseDTO, lvl4DtoList, finelineNbr));
		return lvl4DtoList;
	}

	private void setLvl4SP(ReplenishmentResponseDTO replenishmentResponseDTO, List<Lvl4Dto> lvl4DtoList,
			Integer finelineNbr) {
		Lvl4Dto lvl4 = new Lvl4Dto();
		lvl4.setLvl4Nbr(replenishmentResponseDTO.getLvl4Nbr());
		lvl4.setLvl4Desc(replenishmentResponseDTO.getLvl4Desc());
 
		if (finelineNbr == null) {
			List<FinelineDto> finelineDtoList = mapReplenishmentFl(replenishmentResponseDTO, lvl4, finelineNbr);
		    MetricsDto metricsDto = buyQuantityMapper.fineLineMetricsAggregateQtys(finelineDtoList);
			lvl4.setMetrics(metricsDto);
			lvl4.setFinelines(finelineDtoList);
		} else {
			lvl4.setFinelines(mapReplenishmentFl(replenishmentResponseDTO, lvl4, finelineNbr));
		}
		lvl4DtoList.add(lvl4);
	}

	private List<FinelineDto> mapReplenishmentFl(ReplenishmentResponseDTO replenishmentResponseDTO, Lvl4Dto lvl4,
			Integer finelineNbr) {
		List<FinelineDto> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

		finelineDtoList.stream()
				.filter(finelineDto -> replenishmentResponseDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr()))
				.findFirst().ifPresentOrElse(finelineDto -> {
					if (finelineNbr != null) {
						finelineDto
								.setStyles(mapReplenishmentStyles(replenishmentResponseDTO, finelineDto, finelineNbr));
					}
				}, () -> setFinelineSP(replenishmentResponseDTO, finelineDtoList, finelineNbr));
		return finelineDtoList;
	}

	private void setFinelineSP(ReplenishmentResponseDTO replenishmentResponseDTO, List<FinelineDto> finelineDtoList,
			Integer finelineNbr) {
		FinelineDto fineline = new FinelineDto();
		fineline.setFinelineNbr(replenishmentResponseDTO.getFinelineNbr());
		fineline.setFinelineDesc(replenishmentResponseDTO.getFinelineDesc());
		fineline.setFinelineAltDesc(replenishmentResponseDTO.getFinelineAltDesc());
		if (finelineNbr == null) {		
			fineline.setMetrics(metricsMethodDto(replenishmentResponseDTO));
		} else {
			fineline.setStyles(mapReplenishmentStyles(replenishmentResponseDTO, fineline, finelineNbr));
		}
		finelineDtoList.add(fineline);
	}

	private List<StyleDto> mapReplenishmentStyles(ReplenishmentResponseDTO replenishmentResponseDTO,
			FinelineDto fineline, Integer finelineNbr) {
		List<StyleDto> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

		styleDtoList.stream().filter(styleDto -> replenishmentResponseDTO.getStyleNbr().equals(styleDto.getStyleNbr()))
				.findFirst().ifPresentOrElse(
						styleDto -> styleDto.setCustomerChoices(
								mapReplenishmentCc(replenishmentResponseDTO, styleDto, finelineNbr)),
						() -> setStyleSP(replenishmentResponseDTO, styleDtoList, finelineNbr));
		return styleDtoList;
	}

	private void setStyleSP(ReplenishmentResponseDTO replenishmentResponseDTO, List<StyleDto> styleDtoList,
			Integer finelineNbr) {

		StyleDto styleDto = new StyleDto();
		styleDto.setStyleNbr(replenishmentResponseDTO.getStyleNbr());
		if (finelineNbr == null) {
			
			styleDto.setCustomerChoices(mapReplenishmentCc(replenishmentResponseDTO, styleDto, finelineNbr));
			styleDto.setMetrics(metricsMethodDto(replenishmentResponseDTO));

		} else {
			styleDto.setCustomerChoices(mapReplenishmentCc(replenishmentResponseDTO, styleDto, finelineNbr));
		}
		styleDtoList.add(styleDto);
	}

	private List<CustomerChoiceDto> mapReplenishmentCc(ReplenishmentResponseDTO replenishmentResponseDTO,
			StyleDto styleDto, Integer finelineNbr) {
		List<CustomerChoiceDto> customerChoiceList = Optional.ofNullable(styleDto.getCustomerChoices())
				.orElse(new ArrayList<>());

		customerChoiceList.stream()
				.filter(customerChoiceDto -> replenishmentResponseDTO.getCcId().equals(customerChoiceDto.getCcId()))
				.findFirst().ifPresentOrElse(customerChoiceDto -> {
					if (finelineNbr != null) {
						customerChoiceDto.setMerchMethods(mapMerchMethod(replenishmentResponseDTO, customerChoiceDto));
					}
				}, () -> setCcSP(replenishmentResponseDTO, customerChoiceList, finelineNbr));
		return customerChoiceList;

	}

	private void setCcSP(ReplenishmentResponseDTO replenishmentResponseDTO,
			List<CustomerChoiceDto> customerChoiceDtoList, Integer finelineNbr) {

		CustomerChoiceDto customerChoiceDto = new CustomerChoiceDto();
		customerChoiceDto.setCcId(replenishmentResponseDTO.getCcId());
		if (finelineNbr == null) {			
			customerChoiceDto.setMetrics(metricsMethodDto(replenishmentResponseDTO));
		} else {
			customerChoiceDto.setMerchMethods(mapMerchMethod(replenishmentResponseDTO, customerChoiceDto));
		}
		customerChoiceDtoList.add(customerChoiceDto);
	}
	private List<MerchMethodsDto> mapMerchMethod(ReplenishmentResponseDTO replenishmentResponseDTO,
			CustomerChoiceDto customerChoiceDto) {
		List<MerchMethodsDto> merchMethodsDtoList = Optional.ofNullable(customerChoiceDto.getMerchMethods())
				.orElse(new ArrayList<>());

		merchMethodsDtoList.stream()
				.filter(merchMethodsDto -> CommonUtil.getMerchMethod(replenishmentResponseDTO.getMerchMethod())
						.equals(merchMethodsDto.getMerchMethod()))
				.findFirst().ifPresentOrElse(
						merchMethodsDto -> merchMethodsDto.setSizes(mapSize(replenishmentResponseDTO, merchMethodsDto)),
						() -> setMerch(replenishmentResponseDTO, merchMethodsDtoList));
		return merchMethodsDtoList;
	}
	private void setMerch(ReplenishmentResponseDTO replenishmentResponseDTO,
			List<MerchMethodsDto> merchMethodsDtoList) {
		MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
		merchMethodsDto.setMerchMethod(CommonUtil.getMerchMethod(replenishmentResponseDTO.getMerchMethod()));
		merchMethodsDto.setMetrics(metricsMethodDto(replenishmentResponseDTO));
		merchMethodsDto.setSizes(mapSize(replenishmentResponseDTO, merchMethodsDto));
		merchMethodsDtoList.add(merchMethodsDto);
	}
	private List<SizeDto> mapSize(ReplenishmentResponseDTO replenishmentResponseDTO, MerchMethodsDto merchMethodsDto) {
		List<SizeDto> sizeDtoList = Optional.ofNullable(merchMethodsDto.getSizes()).orElse(new ArrayList<>());

		sizeDtoList.stream().filter(sizeDto -> replenishmentResponseDTO.getAhsSizeId().equals(sizeDto.getAhsSizeId()))
				.findFirst().ifPresentOrElse(customerChoiceDto -> log.info("Size implementation"),
						() -> setSizes(replenishmentResponseDTO, sizeDtoList));
		return sizeDtoList;
	}

	private void setSizes(ReplenishmentResponseDTO replenishmentResponseDTO, List<SizeDto> sizeDtoList) {

		SizeDto sizeDto = new SizeDto();
		sizeDto.setAhsSizeId(replenishmentResponseDTO.getAhsSizeId());
		sizeDto.setSizeDesc(replenishmentResponseDTO.getSizeDesc());
		
		sizeDto.setMetrics(metricsMethodDto(replenishmentResponseDTO));

		List<Replenishment> replenishmentList = new ArrayList<>();
		try {
			if (replenishmentResponseDTO.getReplenObject() != null)
				replenishmentList = Arrays.asList(
						objectMapper.readValue(replenishmentResponseDTO.getReplenObject(), Replenishment[].class));
		} catch (JsonMappingException e) {
			log.error("Error mapping json string replenishment object", e);
		} catch (JsonProcessingException e) {
			log.error("Error parsing replenishment object", e);
		}
		sizeDto.setReplenishments(replenishmentList);
		sizeDtoList.add(sizeDto);
	}
	public MetricsDto metricsMethodDto(ReplenishmentResponseDTO replenishmentResponseDTO) {
		MetricsDto metricsDto = new MetricsDto();
		metricsDto.setFinalBuyQty(replenishmentResponseDTO.getCcSpFinalBuyUnits());
		metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getCcSpReplQty());
		metricsDto.setVendorPack(replenishmentResponseDTO.getCcSpVenderPackCount());
		metricsDto.setWarehousePack(replenishmentResponseDTO.getCcSpWhsePackCount());
		metricsDto.setPackRatio(replenishmentResponseDTO.getCcSpVnpkWhpkRatio());
		metricsDto.setReplenishmentPacks(replenishmentResponseDTO.getCcSpReplPack());
		return metricsDto;
	} 
} 