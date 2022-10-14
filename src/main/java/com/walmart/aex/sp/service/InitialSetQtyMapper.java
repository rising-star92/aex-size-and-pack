package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.initialsetqty.InitialSetQtyData;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetQtyMapper {

	public void mapInitialSetQtyLvl2Sp(InitialSetQtyData initialSetQtyData, BuyQtyResponse response) {
		if (response.getPlanId() == null) {
			response.setPlanId(initialSetQtyData.getPlanId());
		}
		if (response.getLvl0Nbr() == null) {
			response.setLvl0Nbr(initialSetQtyData.getLvl0Nbr());
		}
		if (response.getLvl1Nbr() == null) {
			response.setLvl1Nbr(initialSetQtyData.getLvl1Nbr());
		}
		if (response.getLvl2Nbr() == null) {
			response.setLvl2Nbr(initialSetQtyData.getLvl2Nbr());
		}
		response.setLvl3List(mapInitialSetQtyLvl3Sp(initialSetQtyData, response));
	}

	private List<Lvl3Dto> mapInitialSetQtyLvl3Sp(InitialSetQtyData initialSetQtyData, BuyQtyResponse response) {
		List<Lvl3Dto> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

		lvl3List.stream().filter(lvl3 -> initialSetQtyData.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
				.ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapInitialSetQtyLvl4Sp(initialSetQtyData, lvl3)),
						() -> setLvl3SP(initialSetQtyData, lvl3List));
		return lvl3List;
	}

	private void setLvl3SP(InitialSetQtyData initialSetQtyData, List<Lvl3Dto> lvl3List) {
		Lvl3Dto lvl3 = new Lvl3Dto();
		lvl3.setLvl3Nbr(initialSetQtyData.getLvl3Nbr());
		lvl3.setLvl4List(mapInitialSetQtyLvl4Sp(initialSetQtyData, lvl3));
		lvl3List.add(lvl3);
	}

	private List<Lvl4Dto> mapInitialSetQtyLvl4Sp(InitialSetQtyData initialSetQtyData, Lvl3Dto lvl3) {
		List<Lvl4Dto> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

		lvl4DtoList.stream().filter(lvl4 -> initialSetQtyData.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
				.ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapInitialSetQtyFlSp(initialSetQtyData, lvl4)),
						() -> setLvl4SP(initialSetQtyData, lvl4DtoList));

		return lvl4DtoList;
	}

	private void setLvl4SP(InitialSetQtyData initialSetQtyData, List<Lvl4Dto> lvl4DtoList) {
		Lvl4Dto lvl4 = new Lvl4Dto();
		lvl4.setLvl4Nbr(initialSetQtyData.getLvl4Nbr());
		lvl4DtoList.add(lvl4);
		lvl4.setFinelines(mapInitialSetQtyFlSp(initialSetQtyData, lvl4));
	}

	private List<FinelineDto> mapInitialSetQtyFlSp(InitialSetQtyData initialSetQtyData, Lvl4Dto lvl4) {
		List<FinelineDto> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

		finelineDtoList.stream()
				.filter(finelineDto -> initialSetQtyData.getFinelineNbr().equals(finelineDto.getFinelineNbr()))
				.findFirst().ifPresentOrElse(finelineDto -> {
					finelineDto.setStyles(mapInitialSetQtyStyles(initialSetQtyData, finelineDto));
				}, () -> setFinelineSP(initialSetQtyData, finelineDtoList));

		return finelineDtoList;
	}

	private void setFinelineSP(InitialSetQtyData initialSetQtyData, List<FinelineDto> finelineDtoList) {
		FinelineDto fineline = new FinelineDto();
		fineline.setFinelineNbr(initialSetQtyData.getFinelineNbr());
		fineline.setStyles(mapInitialSetQtyStyles(initialSetQtyData, fineline));
		finelineDtoList.add(fineline);
	}

	private List<StyleDto> mapInitialSetQtyStyles(InitialSetQtyData initialSetQtyData, FinelineDto fineline) {
		List<StyleDto> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

		styleDtoList.stream().filter(styleDto -> initialSetQtyData.getStyleNbr().equals(styleDto.getStyleNbr()))
				.findFirst().ifPresentOrElse(
						styleDto -> styleDto.setCustomerChoices(mapInitialSetQtyCc(initialSetQtyData, styleDto)),
						() -> setStyleSP(initialSetQtyData, styleDtoList));

		return styleDtoList;
	}

	private void setStyleSP(InitialSetQtyData initialSetQtyData, List<StyleDto> styleDtoList) {
		StyleDto styleDto = new StyleDto();
		styleDto.setStyleNbr(initialSetQtyData.getStyleNbr());
		styleDto.setCustomerChoices(mapInitialSetQtyCc(initialSetQtyData, styleDto));
		styleDtoList.add(styleDto);
	}

	private List<CustomerChoiceDto> mapInitialSetQtyCc(InitialSetQtyData initialSetQtyData, StyleDto styleDto) {
		List<CustomerChoiceDto> customerChoiceList = Optional.ofNullable(styleDto.getCustomerChoices())
				.orElse(new ArrayList<>());

		customerChoiceList.stream()
				.filter(customerChoiceDto -> initialSetQtyData.getCcId().equals(customerChoiceDto.getCcId()))
				.findFirst().ifPresentOrElse(customerChoiceDto -> {

					customerChoiceDto.setMerchMethods(mapMerchMethod(initialSetQtyData, customerChoiceDto));

				}, () -> setCcSP(initialSetQtyData, customerChoiceList));

		return customerChoiceList;
	}

	private void setCcSP(InitialSetQtyData initialSetQtyData, List<CustomerChoiceDto> customerChoiceList) {
		CustomerChoiceDto customerChoiceDto = new CustomerChoiceDto();
		customerChoiceDto.setCcId(initialSetQtyData.getCcId());
		customerChoiceDto.setMerchMethods(mapMerchMethod(initialSetQtyData, customerChoiceDto));
		customerChoiceList.add(customerChoiceDto);
	}

	private List<MerchMethodsDto> mapMerchMethod(InitialSetQtyData initialSetQtyData,
			CustomerChoiceDto customerChoiceDto) {
		List<MerchMethodsDto> merchMethodsDtoList = Optional.ofNullable(customerChoiceDto.getMerchMethods())
				.orElse(new ArrayList<>());

		merchMethodsDtoList.stream().filter(
				merchMethodsDto -> initialSetQtyData.getMerchMethodDesc().equals(merchMethodsDto.getMerchMethod()))
				.findFirst().ifPresentOrElse(
						merchMethodsDto -> merchMethodsDto.setSizes(mapSize(initialSetQtyData, merchMethodsDto)),
						() -> setMerch(initialSetQtyData, merchMethodsDtoList));

		return merchMethodsDtoList;
	}

	private void setMerch(InitialSetQtyData initialSetQtyData, List<MerchMethodsDto> merchMethodsDtoList) {
		MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
		merchMethodsDto.setMerchMethod(initialSetQtyData.getMerchMethodDesc());
		merchMethodsDto.setSizes(mapSize(initialSetQtyData, merchMethodsDto));
		merchMethodsDtoList.add(merchMethodsDto);
	}

	private List<SizeDto> mapSize(InitialSetQtyData initialSetQtyData, MerchMethodsDto merchMethodsDto) {
		List<SizeDto> sizeDtoList = Optional.ofNullable(merchMethodsDto.getSizes()).orElse(new ArrayList<>());

		sizeDtoList.stream().filter(sizeDto -> initialSetQtyData.getSizeDesc().equals(sizeDto.getSizeDesc()))
				.findFirst().ifPresentOrElse(customerChoiceDto -> log.info("Size implementation"),
						() -> setSizes(initialSetQtyData, sizeDtoList));

		return sizeDtoList;
	}

	private void setSizes(InitialSetQtyData initialSetQtyData, List<SizeDto> sizeDtoList) {
		SizeDto sizeDto = new SizeDto();
		sizeDto.setAhsSizeId(initialSetQtyData.getAhsSizeId());
		sizeDto.setSizeDesc(initialSetQtyData.getSizeDesc());
		MetricsDto metricsDto = new MetricsDto();
		metricsDto.setFinalInitialSetQty(initialSetQtyData.getFinalInitialSetQty());
		sizeDto.setMetrics(metricsDto);
		sizeDtoList.add(sizeDto);
	}

}
