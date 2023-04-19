package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.dto.replenishment.cons.*;
import com.walmart.aex.sp.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class BuyQtyReplenishmentMapperServiceTest {
	
	@InjectMocks
	@Spy
	BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService;
	
	@Mock
	StyleDto styleDto;
	@Mock
	MerchMethodsDto merchMethodsDto;
	@Mock
	CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest;
	@Mock
	CalculateBuyQtyResponse calculateBuyQtyResponse;
	@Mock
	CustomerChoiceDto customerChoiceDto;
	@Mock
	Set<CcSpMmReplPack> ccSpMmReplPacks;

	@Mock
	ReplenishmentCons replenishmentCons;
	
	@Test
	public void testSetAllReplenishments() {
		calculateBuyQtyParallelRequest = new CalculateBuyQtyParallelRequest();
		calculateBuyQtyParallelRequest.setPlanId(12l);
		calculateBuyQtyParallelRequest.setChannel("store");
		calculateBuyQtyParallelRequest.setFinelineNbr(2968);
		calculateBuyQtyParallelRequest.setLvl0Nbr(50000);
		calculateBuyQtyParallelRequest.setLvl1Nbr(34);
		calculateBuyQtyParallelRequest.setLvl2Nbr(6419);
		calculateBuyQtyParallelRequest.setLvl3Nbr(1652);
		
		List<MerchCatgReplPack> merchCatgReplPacks  = new ArrayList<>();
		MerchCatgReplPack merchCatgReplPack = new MerchCatgReplPack();
		MerchCatgReplPackId merchCatgReplPackId = new MerchCatgReplPackId();
		Set<SubCatgReplPack> subReplPack = new HashSet<>();
		merchCatgReplPack.setSubReplPack(subReplPack);
		merchCatgReplPackId.setPlanId(12l);
		merchCatgReplPackId.setFixtureTypeRollupId(1);
		merchCatgReplPackId.setRepTLvl0(5000);
		merchCatgReplPackId.setRepTLvl1(35);
		merchCatgReplPack.setFinalBuyUnits(1);
		merchCatgReplPack.setReplPackCnt(12);
		merchCatgReplPack.setRunStatusCode(2);
		merchCatgReplPack.setVnpkWhpkRatio(23.0);
		merchCatgReplPack.setWhsePackCnt(44);
		merchCatgReplPack.setVendorPackCnt(4);
		merchCatgReplPack.setMerchCatgReplPackId(merchCatgReplPackId);
		merchCatgReplPacks.add(merchCatgReplPack);
		calculateBuyQtyResponse =  new CalculateBuyQtyResponse();
		calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
		
		SubCatgReplPack subCatgReplPack = new SubCatgReplPack();
		Set<FinelineReplPack> finelineReplPackList = new HashSet<>();
		SubCatgReplPackId SubCatgReplPackId = new SubCatgReplPackId();
		subCatgReplPack.setFinalBuyUnits(2);
		subCatgReplPack.setReplPackCnt(3);
		subCatgReplPack.setReplPackCnt(2);
		subCatgReplPack.setVendorPackCnt(23);
		subCatgReplPack.setWhsePackCnt(2);
		SubCatgReplPackId.setMerchCatgReplPackId(merchCatgReplPackId);
		subCatgReplPack.setSubCatgReplPackId(SubCatgReplPackId);
		subCatgReplPack.setFinelineReplPack(finelineReplPackList);
		subReplPack.add(subCatgReplPack);
		
		FinelineReplPack finelineReplPack = new FinelineReplPack();
		FinelineReplPackId finelineReplPackId = new FinelineReplPackId();
		Set<StyleReplPack> styleReplPackList = new HashSet<>();
		finelineReplPack.setFinalBuyUnits(2);
		finelineReplPack.setReplPackCnt(3);
		finelineReplPack.setReplPackCnt(2);
		finelineReplPack.setVendorPackCnt(23);
		finelineReplPack.setWhsePackCnt(2);
		finelineReplPackId.setFinelineNbr(2968);
		finelineReplPackId.setSubCatgReplPackId(SubCatgReplPackId);
		finelineReplPack.setFinelineReplPackId(finelineReplPackId);
		finelineReplPack.setStyleReplPack(styleReplPackList);
		finelineReplPackList.add(finelineReplPack);
		
		StyleReplPack styleReplPack = new StyleReplPack();
		StyleReplPackId styleReplPackId = new StyleReplPackId();
		Set<CcReplPack> ccReplPackList = new HashSet<>();
		styleReplPack.setFinalBuyUnits(2);
		styleReplPack.setReplPackCnt(3);
		styleReplPack.setReplPackCnt(2);
		styleReplPack.setVendorPackCnt(23);
		styleReplPack.setWhsePackCnt(2);
		styleReplPackId.setFinelineReplPackId(finelineReplPackId);
		styleReplPack.setStyleReplPackId(styleReplPackId);
		styleReplPack.setCcReplPack(ccReplPackList);
		styleReplPackList.add(styleReplPack);
		
		CcReplPack ccReplPack = new CcReplPack();
		CcReplPackId ccReplPackId = new CcReplPackId();
		Set<CcMmReplPack> ccMmReplPackList = new HashSet<>();
		ccReplPack.setFinalBuyUnits(2);
		ccReplPack.setReplPackCnt(3);
		ccReplPack.setReplPackCnt(2);
		ccReplPack.setVendorPackCnt(23);
		ccReplPack.setWhsePackCnt(2);
		ccReplPackId.setStyleReplPackId(styleReplPackId);
		ccReplPack.setCcReplPackId(ccReplPackId);
		ccReplPack.setCcMmReplPack(ccMmReplPackList);
		ccReplPackList.add(ccReplPack);
		
		CcMmReplPack ccMmReplPack = new CcMmReplPack();
		CcMmReplPackId ccMmReplPackId = new CcMmReplPackId();
		ccSpMmReplPacks = new HashSet<>();
		ccMmReplPack.setFinalBuyUnits(2);
		ccMmReplPack.setReplPackCnt(3);
		ccMmReplPack.setReplPackCnt(2);
		ccMmReplPack.setVendorPackCnt(23);
		ccMmReplPack.setWhsePackCnt(2);
		ccMmReplPackId.setCcReplPackId(ccReplPackId);
		ccMmReplPack.setCcMmReplPackId(ccMmReplPackId);
		ccMmReplPack.setCcSpMmReplPack(ccSpMmReplPacks);
		ccMmReplPackList.add(ccMmReplPack);
				
		List<MerchCatgReplPack> catgReplPacks=buyQtyReplenishmentMapperService.setAllReplenishments(styleDto, merchMethodsDto, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks, getReplenishmentCons());
	
		assertNotNull(catgReplPacks);
		assertEquals(1, catgReplPacks.get(0).getFinalBuyUnits());
		assertEquals(12, catgReplPacks.get(0).getReplPackCnt());
		assertEquals(2, catgReplPacks.get(0).getRunStatusCode());
		assertEquals(23.0, catgReplPacks.get(0).getVnpkWhpkRatio());
		assertEquals(44, catgReplPacks.get(0).getWhsePackCnt());
		assertEquals(4, catgReplPacks.get(0).getVendorPackCnt());	
		
	}

	@Test
	void testSetAllReplenishmentsDefaultValues() {
		calculateBuyQtyParallelRequest = new CalculateBuyQtyParallelRequest();
		calculateBuyQtyParallelRequest.setPlanId(12l);
		calculateBuyQtyParallelRequest.setChannel("store");
		calculateBuyQtyParallelRequest.setFinelineNbr(2968);
		calculateBuyQtyParallelRequest.setLvl0Nbr(50000);
		calculateBuyQtyParallelRequest.setLvl1Nbr(34);
		calculateBuyQtyParallelRequest.setLvl2Nbr(6419);
		calculateBuyQtyParallelRequest.setLvl3Nbr(1652);

		List<MerchCatgReplPack> merchCatgReplPacks  = new ArrayList<>();
		calculateBuyQtyResponse =  new CalculateBuyQtyResponse();
		calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);

		List<MerchCatgReplPack> catgReplPacks=buyQtyReplenishmentMapperService.setAllReplenishments(styleDto, merchMethodsDto, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks, getReplenishmentCons());

		assertNotNull(catgReplPacks);
		assertEquals(12, catgReplPacks.get(0).getVendorPackCnt());
		assertEquals(2, catgReplPacks.get(0).getWhsePackCnt());
		assertEquals(6, catgReplPacks.get(0).getVnpkWhpkRatio());
		assertEquals(12, catgReplPacks.get(0).getSubReplPack().iterator().next().getVendorPackCnt());
		assertEquals(2, catgReplPacks.get(0).getSubReplPack().iterator().next().getWhsePackCnt());
		assertEquals(6, catgReplPacks.get(0).getSubReplPack().iterator().next().getVnpkWhpkRatio());
		assertEquals(12, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getVendorPackCnt());
		assertEquals(2, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getWhsePackCnt());
		assertEquals(6, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getVnpkWhpkRatio());
		assertEquals(12, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getVendorPackCnt());
		assertEquals(2, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getWhsePackCnt());
		assertEquals(6, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getVnpkWhpkRatio());
		assertEquals(12, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getCcReplPack().iterator().next()
				.getVendorPackCnt());
		assertEquals(2, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getCcReplPack().iterator().next()
				.getWhsePackCnt());
		assertEquals(6, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getCcReplPack().iterator().next()
				.getVnpkWhpkRatio());
		assertEquals(12, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getCcReplPack().iterator().next()
				.getCcMmReplPack().iterator().next().getVendorPackCnt());
		assertEquals(2, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getCcReplPack().iterator().next()
				.getCcMmReplPack().iterator().next().getWhsePackCnt());
		assertEquals(6, catgReplPacks.get(0).getSubReplPack().iterator().next().getFinelineReplPack()
				.iterator().next().getStyleReplPack().iterator().next().getCcReplPack().iterator().next()
				.getCcMmReplPack().iterator().next().getVnpkWhpkRatio());

	}

	private ReplenishmentCons getReplenishmentCons() {
		ReplenishmentCons replenishmentCons = new ReplenishmentCons();
		MerchCatgReplPackCons merchCatgReplPackCons = new MerchCatgReplPackCons();
		merchCatgReplPackCons.setVendorPackCount(12);
		merchCatgReplPackCons.setWarehousePackCount(2);
		merchCatgReplPackCons.setVendorPackWareHousePackRatio(6.00);
		replenishmentCons.setMerchCatgReplPackCons(merchCatgReplPackCons);
		SubCatgReplPackCons subCatgReplPackCons = new SubCatgReplPackCons();
		subCatgReplPackCons.setVendorPackCount(12);
		subCatgReplPackCons.setWarehousePackCount(2);
		subCatgReplPackCons.setVendorPackWareHousePackRatio(6.00);
		replenishmentCons.setSubCatgReplPackCons(subCatgReplPackCons);
		FinelineReplPackCons finelineReplPackCons = new FinelineReplPackCons();
		finelineReplPackCons.setVendorPackCount(12);
		finelineReplPackCons.setWarehousePackCount(2);
		finelineReplPackCons.setVendorPackWareHousePackRatio(6.00);
		replenishmentCons.setFinelineReplPackCons(finelineReplPackCons);
		StyleReplPackCons styleReplPackCons = new StyleReplPackCons();
		styleReplPackCons.setVendorPackCount(12);
		styleReplPackCons.setWarehousePackCount(2);
		styleReplPackCons.setVendorPackWareHousePackRatio(6.00);
		replenishmentCons.setStyleReplPackCons(styleReplPackCons);
		CcReplPackCons ccReplPackCons = new CcReplPackCons();
		ccReplPackCons.setVendorPackCount(12);
		ccReplPackCons.setWarehousePackCount(2);
		ccReplPackCons.setVendorPackWareHousePackRatio(6.00);
		replenishmentCons.setCcReplPackCons(ccReplPackCons);
		CcMmReplPackCons ccMmReplPackCons = new CcMmReplPackCons();
		ccMmReplPackCons.setVendorPackCount(12);
		ccMmReplPackCons.setWarehousePackCount(2);
		ccMmReplPackCons.setVendorPackWareHousePackRatio(6.00);
		replenishmentCons.setCcMmReplPackCons(ccMmReplPackCons);
		replenishmentCons.setCcSpMmReplPackConsMap(new HashMap<>());
		return replenishmentCons;
	}
}
