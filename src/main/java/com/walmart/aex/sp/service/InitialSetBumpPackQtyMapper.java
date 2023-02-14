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
import com.walmart.aex.sp.dto.initsetbumppkqty.InitialSetBumpPackQtyData;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialSetBumpPackQtyMapper {

	public void mapInitSetBpPkQtyLvl2Sp(InitialSetBumpPackQtyData initSetBpPkQtyData, BuyQtyResponse response) {
		if (response.getPlanId() == null) {
			response.setPlanId(initSetBpPkQtyData.getPlanId());
		}
		if (response.getLvl0Nbr() == null) {
			response.setLvl0Nbr(initSetBpPkQtyData.getLvl0Nbr());
		}
		if (response.getLvl1Nbr() == null) {
			response.setLvl1Nbr(initSetBpPkQtyData.getLvl1Nbr());
		}
		if (response.getLvl2Nbr() == null) {
			response.setLvl2Nbr(initSetBpPkQtyData.getLvl2Nbr());
		}
		response.setLvl3List(mapInitSetBpPkQtyLvl3Sp(initSetBpPkQtyData, response));
	}

	private List<Lvl3Dto> mapInitSetBpPkQtyLvl3Sp(InitialSetBumpPackQtyData initSetBpPkQtyData, BuyQtyResponse response) {
		List<Lvl3Dto> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

		lvl3List.stream().filter(lvl3 -> initSetBpPkQtyData.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
				.ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapInitSetBpPkQtyLvl4Sp(initSetBpPkQtyData, lvl3)),
						() -> setLvl3SP(initSetBpPkQtyData, lvl3List));
		return lvl3List;
	}

	private void setLvl3SP(InitialSetBumpPackQtyData initSetBpPkQtyData, List<Lvl3Dto> lvl3List) {
		Lvl3Dto lvl3 = new Lvl3Dto();
		lvl3.setLvl3Nbr(initSetBpPkQtyData.getLvl3Nbr());
		lvl3.setLvl4List(mapInitSetBpPkQtyLvl4Sp(initSetBpPkQtyData, lvl3));
		lvl3List.add(lvl3);
	}

	private List<Lvl4Dto> mapInitSetBpPkQtyLvl4Sp(InitialSetBumpPackQtyData initSetBpPkQtyData, Lvl3Dto lvl3) {
		List<Lvl4Dto> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

		lvl4DtoList.stream().filter(lvl4 -> initSetBpPkQtyData.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
				.ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapInitSetBpPkQtyFlSp(initSetBpPkQtyData, lvl4)),
						() -> setLvl4SP(initSetBpPkQtyData, lvl4DtoList));

		return lvl4DtoList;
	}

	private void setLvl4SP(InitialSetBumpPackQtyData initSetBpPkQtyData, List<Lvl4Dto> lvl4DtoList) {
		Lvl4Dto lvl4 = new Lvl4Dto();
		lvl4.setLvl4Nbr(initSetBpPkQtyData.getLvl4Nbr());
		lvl4DtoList.add(lvl4);
		lvl4.setFinelines(mapInitSetBpPkQtyFlSp(initSetBpPkQtyData, lvl4));
	}

	private List<FinelineDto> mapInitSetBpPkQtyFlSp(InitialSetBumpPackQtyData initSetBpPkQtyData, Lvl4Dto lvl4) {
		List<FinelineDto> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

		finelineDtoList.stream()
				.filter(finelineDto -> initSetBpPkQtyData.getFinelineNbr().equals(finelineDto.getFinelineNbr()))
				.findFirst().ifPresentOrElse(
						finelineDto -> finelineDto.setStyles(mapInitSetBpPkQtyStyles(initSetBpPkQtyData, finelineDto)),
						() -> setFinelineSP(initSetBpPkQtyData, finelineDtoList));

		return finelineDtoList;
	}

	private void setFinelineSP(InitialSetBumpPackQtyData initSetBpPkQtyData, List<FinelineDto> finelineDtoList) {
		FinelineDto fineline = new FinelineDto();
		fineline.setFinelineNbr(initSetBpPkQtyData.getFinelineNbr());
		fineline.setStyles(mapInitSetBpPkQtyStyles(initSetBpPkQtyData, fineline));
		finelineDtoList.add(fineline);
	}

	private List<StyleDto> mapInitSetBpPkQtyStyles(InitialSetBumpPackQtyData initSetBpPkQtyData, FinelineDto fineline) {
		List<StyleDto> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

		styleDtoList.stream().filter(styleDto -> initSetBpPkQtyData.getStyleNbr().equals(styleDto.getStyleNbr()))
				.findFirst().ifPresentOrElse(
						styleDto -> styleDto.setCustomerChoices(mapInitSetBpPkQtyCc(initSetBpPkQtyData, styleDto)),
						() -> setStyleSP(initSetBpPkQtyData, styleDtoList));

		return styleDtoList;
	}

	private void setStyleSP(InitialSetBumpPackQtyData initSetBpPkQtyData, List<StyleDto> styleDtoList) {
		StyleDto styleDto = new StyleDto();
		styleDto.setStyleNbr(initSetBpPkQtyData.getStyleNbr());
		styleDto.setCustomerChoices(mapInitSetBpPkQtyCc(initSetBpPkQtyData, styleDto));
		styleDtoList.add(styleDto);
	}

	private List<CustomerChoiceDto> mapInitSetBpPkQtyCc(InitialSetBumpPackQtyData initSetBpPkQtyData, StyleDto styleDto) {
		List<CustomerChoiceDto> customerChoiceList = Optional.ofNullable(styleDto.getCustomerChoices())
				.orElse(new ArrayList<>());

		customerChoiceList.stream()
				.filter(customerChoiceDto -> initSetBpPkQtyData.getCcId().equals(customerChoiceDto.getCcId()))
				.findFirst().ifPresentOrElse(customerChoiceDto -> 
					customerChoiceDto.setMerchMethods(mapMerchMethod(initSetBpPkQtyData, customerChoiceDto)), 
					() -> setCcSP(initSetBpPkQtyData, customerChoiceList));

		return customerChoiceList;
	}

	private void setCcSP(InitialSetBumpPackQtyData initSetBpPkQtyData, List<CustomerChoiceDto> customerChoiceList) {
		CustomerChoiceDto customerChoiceDto = new CustomerChoiceDto();
		customerChoiceDto.setCcId(initSetBpPkQtyData.getCcId());
		customerChoiceDto.setMerchMethods(mapMerchMethod(initSetBpPkQtyData, customerChoiceDto));
		customerChoiceList.add(customerChoiceDto);
	}

	private List<MerchMethodsDto> mapMerchMethod(InitialSetBumpPackQtyData initSetBpPkQtyData,
			CustomerChoiceDto customerChoiceDto) {
		List<MerchMethodsDto> merchMethodsDtoList = Optional.ofNullable(customerChoiceDto.getMerchMethods())
				.orElse(new ArrayList<>());

		merchMethodsDtoList.stream().filter(
				merchMethodsDto -> initSetBpPkQtyData.getMerchMethodDesc().equals(merchMethodsDto.getMerchMethod()))
				.findFirst().ifPresentOrElse(
						merchMethodsDto -> merchMethodsDto.setSizes(mapSize(initSetBpPkQtyData, merchMethodsDto)),
						() -> setMerch(initSetBpPkQtyData, merchMethodsDtoList));

		return merchMethodsDtoList;
	}

	private void setMerch(InitialSetBumpPackQtyData initSetBpPkQtyData, List<MerchMethodsDto> merchMethodsDtoList) {
		MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
		merchMethodsDto.setMerchMethod(initSetBpPkQtyData.getMerchMethodDesc());
		merchMethodsDto.setSizes(mapSize(initSetBpPkQtyData, merchMethodsDto));
		merchMethodsDtoList.add(merchMethodsDto);
	}

	private List<SizeDto> mapSize(InitialSetBumpPackQtyData initSetBpPkQtyData, MerchMethodsDto merchMethodsDto) {
		List<SizeDto> sizeDtoList = Optional.ofNullable(merchMethodsDto.getSizes()).orElse(new ArrayList<>());

		sizeDtoList.stream().filter(sizeDto -> initSetBpPkQtyData.getSizeDesc().equals(sizeDto.getSizeDesc()))
				.findFirst().ifPresentOrElse(sizeDto -> updateQuantity(initSetBpPkQtyData, sizeDto),
						() -> setSizes(initSetBpPkQtyData, sizeDtoList));

		return sizeDtoList;
	}

	private void setSizes(InitialSetBumpPackQtyData initSetBpPkQtyData, List<SizeDto> sizeDtoList) {
		SizeDto sizeDto = new SizeDto();
		sizeDto.setAhsSizeId(initSetBpPkQtyData.getAhsSizeId());
		sizeDto.setSizeDesc(initSetBpPkQtyData.getSizeDesc());
		MetricsDto metricsDto = new MetricsDto();
		metricsDto.setFinalInitialSetQty(initSetBpPkQtyData.getFinalInitialSetQty());
		metricsDto.setBumpPackQty(initSetBpPkQtyData.getBumpPackQty());
		sizeDto.setMetrics(metricsDto);
		sizeDtoList.add(sizeDto);
	}

	private void updateQuantity(InitialSetBumpPackQtyData initSetBpPkQtyData, SizeDto sizeDto) {
		if (initSetBpPkQtyData.getFinalInitialSetQty() != null)
			sizeDto.getMetrics().setFinalInitialSetQty(sizeDto.getMetrics().getFinalInitialSetQty() + initSetBpPkQtyData.getFinalInitialSetQty());
		if (initSetBpPkQtyData.getBumpPackQty() != null)
			sizeDto.getMetrics().setBumpPackQty(sizeDto.getMetrics().getBumpPackQty() + initSetBpPkQtyData.getBumpPackQty());
	}

}
