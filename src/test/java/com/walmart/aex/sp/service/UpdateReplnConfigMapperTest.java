package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.FinelineReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.SubCatgReplPack;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateReplnConfigMapperTest {
	
	private static final Integer vnpk=500;
	private static final Integer whpk=500;
	Double vnpkwhpkRatio=1d;
	
	@InjectMocks
	@Spy
	private UpdateReplnConfigMapper replenishmentMapper;
		
	@Mock
	private StyleReplnPkConsRepository styleReplnConsRepository;
	
	@Mock
	private List<FinelineReplPack> finelineReplnPkConsList;
	
	@Mock
	private FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	
	@Mock
	private CcReplnPkConsRepository ccReplnConsRepository;
		
	@Mock
	private List<MerchCatgReplPack> catgReplnPkConsList;
	
	@Mock
	private CatgReplnPkConsRepository catgReplnPkConsRepository;
	
	@Mock
	private List<SubCatgReplPack> SubcatgReplnPkConsList;
	
	@Mock
	private SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	
	@Mock
	private CcMmReplnPkConsRepository ccMmRepln;
	
	@Mock
	private List<CcSpMmReplPack> ccSpReplnPkConsList1;

	@Mock
	private CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

	@Spy
	private ReplenishmentsOptimizationService replenishmentsOptimizationService;


	@Captor
	private ArgumentCaptor<CcSpMmReplPack> ccSpMmReplPackArgumentCaptor;

	@Spy
	private ObjectMapper objectMapper;
	
	@Test
	void testUpdateVnpkWhpkForStyleReplnConsMapper() {
		
		catgReplnPkConsList = new ArrayList<>();
		MerchCatgReplPack merch = new MerchCatgReplPack();
		Set<SubCatgReplPack> subReplenishmentPack =new HashSet<>();
		SubCatgReplPack sub = new SubCatgReplPack();
		merch.setVendorPackCnt(vnpk);
		merch.setWhsePackCnt(whpk);
		merch.setVnpkWhpkRatio(vnpkwhpkRatio);
		merch.setSubReplPack(subReplenishmentPack);
		subReplenishmentPack.add(sub);
		catgReplnPkConsList.add(merch);
		
		SubcatgReplnPkConsList = new ArrayList<>();
		SubCatgReplPack sub1 = new SubCatgReplPack();
		Set<FinelineReplPack> finelineReplenishmentPackList = new HashSet<>();
		FinelineReplPack finelineReplenishmentPack = new FinelineReplPack();
		sub.setVendorPackCnt(vnpk);
		sub.setWhsePackCnt(whpk);
		sub.setVnpkWhpkRatio(vnpkwhpkRatio);
		sub.setFinelineReplPack(finelineReplenishmentPackList);
		finelineReplenishmentPackList.add(finelineReplenishmentPack);
		SubcatgReplnPkConsList.add(sub1);
				
		finelineReplnPkConsList = new ArrayList<>();
		Set<StyleReplPack> styleReplenishmentPack = new HashSet<>();
		StyleReplPack style = new StyleReplPack();
		finelineReplenishmentPack.setVendorPackCnt(vnpk);
		finelineReplenishmentPack.setWhsePackCnt(whpk);
		finelineReplenishmentPack.setVnpkWhpkRatio(vnpkwhpkRatio);
		finelineReplenishmentPack.setStyleReplPack(styleReplenishmentPack);
		styleReplenishmentPack.add(style);
		finelineReplnPkConsList.add(finelineReplenishmentPack);
			
		List<StyleReplPack> styleReplnPkConsList= new ArrayList<>();
		Set<CcReplPack> ccReplenishmentPack = new HashSet<>();
		CcReplPack replenishmentPack = new CcReplPack();
		style.setVendorPackCnt(vnpk);
		style.setWhsePackCnt(whpk);
		style.setVnpkWhpkRatio(vnpkwhpkRatio);
		styleReplnPkConsList.add(style);
		ccReplenishmentPack.add(replenishmentPack);
		style.setCcReplPack(ccReplenishmentPack);
				
		List<CcReplPack> ccReplnPkConsList = new ArrayList<>();
		Set<CcMmReplPack> ccSpReplenishmentPack = new HashSet<>();
		CcMmReplPack cc = new CcMmReplPack();
		replenishmentPack.setVendorPackCnt(vnpk);
		replenishmentPack.setWhsePackCnt(whpk);
		replenishmentPack.setVnpkWhpkRatio(vnpkwhpkRatio);
		replenishmentPack.setCcMmReplPack(ccSpReplenishmentPack);
		ccReplnPkConsList.add(replenishmentPack);
		
		cc.setVendorPackCnt(vnpk);
		cc.setWhsePackCnt(whpk);
		cc.setVnpkWhpkRatio(vnpkwhpkRatio);
		ccSpReplenishmentPack.add(cc);
		cc.setCcReplPack(replenishmentPack);
				
		List<CcMmReplPack> ccMmReplnPkConsList = new ArrayList<>();
		Set<CcSpMmReplPack> ccSpMmReplPack = new HashSet<>();
		cc.setVendorPackCnt(vnpk);
		cc.setWhsePackCnt(whpk);
		cc.setVnpkWhpkRatio(vnpkwhpkRatio);
		cc.setCcSpMmReplPack(ccSpMmReplPack);
		ccMmReplnPkConsList.add(cc);
					
		replenishmentMapper.updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, 500, 500);
		//Assert
    	verify(replenishmentMapper,Mockito.times(1)).updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, vnpk, whpk);
    	
    	assertEquals(catgReplnPkConsList.get(0).getVendorPackCnt(), 500);    	
    	assertEquals(catgReplnPkConsList.get(0).getWhsePackCnt(), 500);
    	assertEquals(catgReplnPkConsList.get(0).getVnpkWhpkRatio(), 1d);
    			
	}

	@Test
	void test_updateVnpkWhpkForCcSpMmReplnPkConsMapperShouldRoundAdjReplnUnits() throws JsonProcessingException {

		List<CcSpMmReplPack> ccSpMmReplPacks = new ArrayList<>();
		CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
		ccSpMmReplPack.setReplPackCnt(6);
		ccSpMmReplPack.setVendorPackCnt(3);
		ccSpMmReplPack.setWhsePackCnt(2);
		ccSpMmReplPack.setReplUnits(4);
		ccSpMmReplPack.setReplenObj("[{\"replnWeek\":1,\"replnWeekDesc\":\"test\",\"replnUnits\":1," +
				"\"adjReplnUnits\":333,\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}," +
				"{\"replnWeek\":2,\"replnWeekDesc\":\"test\",\"replnUnits\":1,\"adjReplnUnits\":100," +
				"\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}]");

		ccSpMmReplPacks.add(ccSpMmReplPack);
		replenishmentMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplPacks, 4,2);
		verify(ccSpReplnPkConsRepository,Mockito.times(1)).save(ccSpMmReplPackArgumentCaptor.capture());
		CcSpMmReplPack ccSpMmReplPack1 = ccSpMmReplPackArgumentCaptor.getValue();
		assertNotNull(ccSpMmReplPack1);
		List<Replenishment> replenishments = Arrays.asList(objectMapper.readValue(ccSpMmReplPack1.getReplenObj(),Replenishment[].class));
		assertEquals(436, replenishments.get(0).getAdjReplnUnits());
		assertEquals(0, replenishments.get(1).getAdjReplnUnits());

	}

	@Test
	void test_updateVnpkWhpkForCcSpMmReplnPkConsMapperShouldSkipRoundingAdjReplnUnits() throws JsonProcessingException {

		List<CcSpMmReplPack> ccSpMmReplPacks = new ArrayList<>();
		CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
		ccSpMmReplPack.setReplPackCnt(6);
		ccSpMmReplPack.setVendorPackCnt(3);
		ccSpMmReplPack.setWhsePackCnt(2);
		ccSpMmReplPack.setReplUnits(4);
		ccSpMmReplPack.setReplenObj("[{\"replnWeek\":1,\"replnWeekDesc\":\"test\",\"replnUnits\":1," +
				"\"adjReplnUnits\":300,\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}," +
				"{\"replnWeek\":2,\"replnWeekDesc\":\"test\",\"replnUnits\":1,\"adjReplnUnits\":100," +
				"\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}]");

		ccSpMmReplPacks.add(ccSpMmReplPack);
		replenishmentMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplPacks, 4,2);
		verify(ccSpReplnPkConsRepository,Mockito.times(1)).save(ccSpMmReplPackArgumentCaptor.capture());
		CcSpMmReplPack ccSpMmReplPack1 = ccSpMmReplPackArgumentCaptor.getValue();
		assertNotNull(ccSpMmReplPack1);
		List<Replenishment> replenishments = Arrays.asList(objectMapper.readValue(ccSpMmReplPack1.getReplenObj(),Replenishment[].class));
		assertEquals(400, replenishments.get(0).getAdjReplnUnits());
		assertEquals(0, replenishments.get(1).getAdjReplnUnits());

	}

}