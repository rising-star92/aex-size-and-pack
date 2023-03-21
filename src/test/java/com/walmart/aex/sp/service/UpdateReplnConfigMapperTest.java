package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
	private CcReplnPkConsRepository ccReplnPkConsRepository;

	@Mock
	private List<MerchCatgReplPack> catgReplnPkConsList;

	@Mock
	private CatgReplnPkConsRepository catgReplnPkConsRepository;

	@Mock
	private List<SubCatgReplPack> SubcatgReplnPkConsList;

	@Mock
	private SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

	@Mock
	private CcMmReplnPkConsRepository ccMmReplnPkConsRepository;

	@Mock
	private List<CcSpMmReplPack> ccSpReplnPkConsList1;

	@Mock
	private CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

	@Mock
	private ReplenishmentsOptimizationService replenishmentsOptimizationService;


	@Captor
	private ArgumentCaptor<List<CcSpMmReplPack>> ccSpMmReplPackArgumentCaptor;
	@Captor
	private ArgumentCaptor<List<CcMmReplPack>> ccMmReplPackArgumentCaptor;
	@Captor
	private ArgumentCaptor<List<CcReplPack>> ccReplPackArgumentCaptor;
	@Captor
	private ArgumentCaptor<List<StyleReplPack>> styleReplPackArgumentCaptor;
	@Captor
	private ArgumentCaptor<List<FinelineReplPack>> finelineReplPackArgumentCaptor;
	@Captor
	private ArgumentCaptor<List<SubCatgReplPack>> subCatgReplPackArgumentCaptor;
	@Captor
	private ArgumentCaptor<List<MerchCatgReplPack>> merchCatgReplPackArgumentCaptor;
	@Spy
	private ObjectMapper objectMapper;
	@Test
	void test_updateVnpkWhpkForCatgReplnConsMapperToRollUpReplPackCnt() {
		CcSpMmReplPack ccSpMmRP1 = new CcSpMmReplPack();
		ccSpMmRP1.setReplPackCnt(6);
		ccSpMmRP1.setVendorPackCnt(3);
		ccSpMmRP1.setWhsePackCnt(2);
		ccSpMmRP1.setReplUnits(4);
		ccSpMmRP1.setReplenObj("[{\"replnWeek\":1,\"replnWeekDesc\":\"test\",\"replnUnits\":1," +
				"\"adjReplnUnits\":333,\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}," +
				"{\"replnWeek\":2,\"replnWeekDesc\":\"test\",\"replnUnits\":1,\"adjReplnUnits\":100," +
				"\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}]");
		ccSpMmRP1.setCcSpReplPackId(getCcSpMmReplPackId(1));
		CcSpMmReplPack ccSpMmRP2 = new CcSpMmReplPack();
		ccSpMmRP2.setReplPackCnt(6);
		ccSpMmRP2.setVendorPackCnt(3);
		ccSpMmRP2.setWhsePackCnt(2);
		ccSpMmRP2.setReplUnits(4);
		ccSpMmRP2.setReplenObj("[{\"replnWeek\":1,\"replnWeekDesc\":\"test\",\"replnUnits\":1," +
				"\"adjReplnUnits\":555,\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}," +
				"{\"replnWeek\":2,\"replnWeekDesc\":\"test\",\"replnUnits\":1,\"adjReplnUnits\":200," +
				"\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}]");
		ccSpMmRP2.setCcSpReplPackId(getCcSpMmReplPackId(1));
		CcSpMmReplPack ccSpMmRP3 = new CcSpMmReplPack();
		ccSpMmRP3.setReplPackCnt(6);
		ccSpMmRP3.setVendorPackCnt(3);
		ccSpMmRP3.setWhsePackCnt(2);
		ccSpMmRP3.setReplUnits(4);
		ccSpMmRP3.setReplenObj("[{\"replnWeek\":1,\"replnWeekDesc\":\"test\",\"replnUnits\":1," +
				"\"adjReplnUnits\":777,\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}," +
				"{\"replnWeek\":2,\"replnWeekDesc\":\"test\",\"replnUnits\":1,\"adjReplnUnits\":300," +
				"\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}]");
		ccSpMmRP3.setCcSpReplPackId(getCcSpMmReplPackId(1));
		CcSpMmReplPack ccSpMmRP4 = new CcSpMmReplPack();
		ccSpMmRP4.setReplPackCnt(6);
		ccSpMmRP4.setVendorPackCnt(3);
		ccSpMmRP4.setWhsePackCnt(2);
		ccSpMmRP4.setReplUnits(4);
		ccSpMmRP4.setReplenObj("[{\"replnWeek\":1,\"replnWeekDesc\":\"test\",\"replnUnits\":1," +
				"\"adjReplnUnits\":999,\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}," +
				"{\"replnWeek\":2,\"replnWeekDesc\":\"test\",\"replnUnits\":1,\"adjReplnUnits\":400," +
				"\"remainingUnits\":1,\"dcInboundUnits\":1,\"dcInboundAdjUnits\":1}]");
		ccSpMmRP4.setCcSpReplPackId(getCcSpMmReplPackId(1));
		Set<CcSpMmReplPack> ccSpMmReplPackSet1 = new LinkedHashSet<>(Arrays.asList(ccSpMmRP1, ccSpMmRP2));
		Set<CcSpMmReplPack> ccSpMmReplPackSet2 = new LinkedHashSet<>(Arrays.asList(ccSpMmRP3, ccSpMmRP4));

		CcMmReplPack cc1 = new CcMmReplPack();
		cc1.setVendorPackCnt(vnpk);
		cc1.setWhsePackCnt(whpk);
		cc1.setVnpkWhpkRatio(vnpkwhpkRatio);
		cc1.setCcSpMmReplPack(ccSpMmReplPackSet1);
		CcMmReplPack cc2 = new CcMmReplPack();
		cc2.setVendorPackCnt(vnpk);
		cc2.setWhsePackCnt(whpk);
		cc2.setVnpkWhpkRatio(vnpkwhpkRatio);
		cc2.setCcSpMmReplPack(ccSpMmReplPackSet2);
		Set<CcMmReplPack> ccSpReplenishmentPack = new LinkedHashSet<>(Arrays.asList(cc1, cc2));

		CcReplPack replenishmentPack1 = new CcReplPack();
		replenishmentPack1.setVendorPackCnt(vnpk);
		replenishmentPack1.setWhsePackCnt(whpk);
		replenishmentPack1.setVnpkWhpkRatio(vnpkwhpkRatio);
		replenishmentPack1.setCcMmReplPack(ccSpReplenishmentPack);
		CcReplPack replenishmentPack2 = new CcReplPack();
		replenishmentPack2.setVendorPackCnt(vnpk);
		replenishmentPack2.setWhsePackCnt(whpk);
		replenishmentPack2.setVnpkWhpkRatio(vnpkwhpkRatio);
		replenishmentPack2.setCcMmReplPack(ccSpReplenishmentPack);
		Set<CcReplPack> ccReplenishmentPack = new LinkedHashSet<>(Arrays.asList(replenishmentPack1, replenishmentPack2));

		StyleReplPack style1 = new StyleReplPack();
		style1.setVendorPackCnt(vnpk);
		style1.setWhsePackCnt(whpk);
		style1.setVnpkWhpkRatio(vnpkwhpkRatio);
		style1.setCcReplPack(ccReplenishmentPack);
		StyleReplPack style2 = new StyleReplPack();
		style2.setVendorPackCnt(vnpk);
		style2.setWhsePackCnt(whpk);
		style2.setVnpkWhpkRatio(vnpkwhpkRatio);
		style2.setCcReplPack(ccReplenishmentPack);
		Set<StyleReplPack> styleReplenishmentPack = new LinkedHashSet<>(Arrays.asList(style1, style2));

		FinelineReplPack finelineReplenishmentPack1 = new FinelineReplPack();
		finelineReplenishmentPack1.setVendorPackCnt(vnpk);
		finelineReplenishmentPack1.setWhsePackCnt(whpk);
		finelineReplenishmentPack1.setVnpkWhpkRatio(vnpkwhpkRatio);
		finelineReplenishmentPack1.setStyleReplPack(styleReplenishmentPack);
		FinelineReplPack finelineReplenishmentPack2 = new FinelineReplPack();
		finelineReplenishmentPack2.setVendorPackCnt(vnpk);
		finelineReplenishmentPack2.setWhsePackCnt(whpk);
		finelineReplenishmentPack2.setVnpkWhpkRatio(vnpkwhpkRatio);
		finelineReplenishmentPack2.setStyleReplPack(styleReplenishmentPack);
		Set<FinelineReplPack> finelineReplenishmentPackList = new LinkedHashSet<>(Arrays.asList(finelineReplenishmentPack1, finelineReplenishmentPack2));

		SubCatgReplPack sub1 = new SubCatgReplPack();
		sub1.setVendorPackCnt(vnpk);
		sub1.setWhsePackCnt(whpk);
		sub1.setVnpkWhpkRatio(vnpkwhpkRatio);
		sub1.setFinelineReplPack(finelineReplenishmentPackList);
		SubCatgReplPack sub2 = new SubCatgReplPack();
		sub2.setVendorPackCnt(vnpk);
		sub2.setWhsePackCnt(whpk);
		sub2.setVnpkWhpkRatio(vnpkwhpkRatio);
		sub2.setFinelineReplPack(finelineReplenishmentPackList);
		Set<SubCatgReplPack> subReplenishmentPack = new LinkedHashSet<>(Arrays.asList(sub1, sub2));

		MerchCatgReplPack merch1 = new MerchCatgReplPack();
		merch1.setVendorPackCnt(vnpk);
		merch1.setWhsePackCnt(whpk);
		merch1.setVnpkWhpkRatio(vnpkwhpkRatio);
		merch1.setSubReplPack(subReplenishmentPack);
		MerchCatgReplPack merch2 = new MerchCatgReplPack();
		merch2.setVendorPackCnt(vnpk);
		merch2.setWhsePackCnt(whpk);
		merch2.setVnpkWhpkRatio(vnpkwhpkRatio);
		merch2.setSubReplPack(subReplenishmentPack);
		List<MerchCatgReplPack> catgReplnPkConsLst = new LinkedList<>(Arrays.asList(merch1, merch2));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(333L,100L), 4, 1, 34, 12L)).thenReturn(getReplenishments(436L,0L));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(555L,200L), 4, 1, 34, 12L)).thenReturn(getReplenishments(756L,0L));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(777L,300L), 4, 1, 34, 12L)).thenReturn(getReplenishments(1080L,0L));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(999L,400L), 4, 1, 34, 12L)).thenReturn(getReplenishments(1400L,0L));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(436L,0L), 4, 1, 34, 12L)).thenReturn(getReplenishments(436L,0L));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(756L,0L), 4, 1, 34, 12L)).thenReturn(getReplenishments(756L,0L));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(1080L,0L), 4, 1, 34, 12L)).thenReturn(getReplenishments(1080L,0L));
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(1400L,0L), 4, 1, 34, 12L)).thenReturn(getReplenishments(1400L,0L));
		replenishmentMapper.updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsLst, 4, 2);
		verify(replenishmentMapper,Mockito.times(1)).updateVnpkWhpkForCatgReplnConsMapper(any(), anyInt(), anyInt());

		verify(catgReplnPkConsRepository,Mockito.times(1)).saveAll(merchCatgReplPackArgumentCaptor.capture());
		verify(subCatgReplnPkConsRepository,Mockito.times(2)).saveAll(subCatgReplPackArgumentCaptor.capture());
		verify(finelineReplnPkConsRepository,Mockito.times(4)).saveAll(finelineReplPackArgumentCaptor.capture());
		verify(styleReplnConsRepository,Mockito.times(8)).saveAll(styleReplPackArgumentCaptor.capture());
		verify(ccReplnPkConsRepository,Mockito.times(16)).saveAll(ccReplPackArgumentCaptor.capture());
		verify(ccMmReplnPkConsRepository,Mockito.times(32)).saveAll(ccMmReplPackArgumentCaptor.capture());
		verify(ccSpReplnPkConsRepository,Mockito.times(64)).saveAll(ccSpMmReplPackArgumentCaptor.capture());

		assertEquals(List.of(58752, 58752), merchCatgReplPackArgumentCaptor.getAllValues().stream()
				.map(val -> val.stream().map(MerchCatgReplPack::getReplUnits).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new)));
		assertEquals(List.of(14688, 14688), merchCatgReplPackArgumentCaptor.getAllValues().stream()
				.map(val -> val.stream().map(MerchCatgReplPack::getReplPackCnt).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new)));

		assertEquals(List.of(29376, 29376, 29376, 29376), subCatgReplPackArgumentCaptor.getAllValues().stream()
				.map(val -> val.stream().map(SubCatgReplPack::getReplUnits).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new)));
		assertEquals(List.of(7344, 7344, 7344, 7344), subCatgReplPackArgumentCaptor.getAllValues().stream()
				.map(val -> val.stream().map(SubCatgReplPack::getReplPackCnt).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new)));

		assertEquals(117504, finelineReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(FinelineReplPack::getReplUnits).sum()).sum());
		assertEquals(29376, finelineReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(FinelineReplPack::getReplPackCnt).sum()).sum());

		assertEquals(117504, styleReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(StyleReplPack::getReplUnits).sum()).sum());
		assertEquals(29376, styleReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(StyleReplPack::getReplPackCnt).sum()).sum());

		assertEquals(117504, ccReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(CcReplPack::getReplUnits).sum()).sum());
		assertEquals(29376, ccReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(CcReplPack::getReplPackCnt).sum()).sum());

		assertEquals(117504, ccMmReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(CcMmReplPack::getReplUnits).sum()).sum());
		assertEquals(29376, ccMmReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(CcMmReplPack::getReplPackCnt).sum()).sum());

		assertEquals(117504, ccSpMmReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(CcSpMmReplPack::getReplUnits).sum()).sum());
		assertEquals(29376, ccSpMmReplPackArgumentCaptor.getAllValues().stream().mapToInt(val -> val.stream().mapToInt(CcSpMmReplPack::getReplPackCnt).sum()).sum());

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
		ccSpMmReplPack.setCcSpReplPackId(getCcSpMmReplPackId(1));
		ccSpMmReplPacks.add(ccSpMmReplPack);
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(getReplenishments(333L,100L), 4, 1, 34, 12L)).thenReturn(getReplenishments(436L,0L));

		replenishmentMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplPacks, 4,2);
		verify(ccSpReplnPkConsRepository,Mockito.times(1)).saveAll(ccSpMmReplPackArgumentCaptor.capture());
		CcSpMmReplPack ccSpMmReplPack1 = ccSpMmReplPackArgumentCaptor.getValue().iterator().next();
		assertNotNull(ccSpMmReplPack1);
		List<Replenishment> replenishments = Arrays.asList(objectMapper.readValue(ccSpMmReplPack1.getReplenObj(),Replenishment[].class));
		assertEquals(436, replenishments.get(0).getAdjReplnUnits());
		assertEquals(0, replenishments.get(1).getAdjReplnUnits());

	}

	@Test
	void test_updateVnpkWhpkForCcSpMmReplnPkConsMapperForOnline() throws JsonProcessingException {

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
		ccSpMmReplPack.setCcSpReplPackId(getCcSpMmReplPackId(2));
		ccSpMmReplPacks.add(ccSpMmReplPack);
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(getReplenishments(336L,100L));
		replenishmentMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplPacks, 4,2);
		verify(ccSpReplnPkConsRepository,Mockito.times(1)).saveAll(ccSpMmReplPackArgumentCaptor.capture());
		CcSpMmReplPack ccSpMmReplPack1 = ccSpMmReplPackArgumentCaptor.getValue().iterator().next();
		assertNotNull(ccSpMmReplPack1);
		List<Replenishment> replenishments = Arrays.asList(objectMapper.readValue(ccSpMmReplPack1.getReplenObj(),Replenishment[].class));
		assertEquals(336, replenishments.get(0).getAdjReplnUnits());
		assertEquals(100, replenishments.get(1).getAdjReplnUnits());

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
		ccSpMmReplPack.setCcSpReplPackId(getCcSpMmReplPackId(1));
		ccSpMmReplPacks.add(ccSpMmReplPack);
		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(getReplenishments(400L,0L));
		replenishmentMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplPacks, 4,2);
		verify(ccSpReplnPkConsRepository,Mockito.times(1)).saveAll(ccSpMmReplPackArgumentCaptor.capture());
		CcSpMmReplPack ccSpMmReplPack1 = ccSpMmReplPackArgumentCaptor.getValue().iterator().next();
		assertNotNull(ccSpMmReplPack1);
		List<Replenishment> replenishments = Arrays.asList(objectMapper.readValue(ccSpMmReplPack1.getReplenObj(), Replenishment[].class));
		assertEquals(400, replenishments.get(0).getAdjReplnUnits());
		assertEquals(0, replenishments.get(1).getAdjReplnUnits());

	}

	@Test
	void test_updateVnpkWhpkForCcSpMmReplnPkConsMapperShouldSetZeroIfAdjReplnUnitsIsNegative() throws IOException {
		List<String> replenObjList = new ArrayList<>();
		try	{
			Scanner scanner = new Scanner(new File("src/test/resources/data/ReplenishmentTestData.txt"));
			while(scanner.hasNextLine()) {
				replenObjList.add(scanner.nextLine());
			}
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
		Set<CcSpMmReplPack> ccSpMmReplPacks=new HashSet<>();
		replenObjList.forEach(val->{
			CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
			ccSpMmReplPack.setReplPackCnt(6);
			ccSpMmReplPack.setVendorPackCnt(12);
			ccSpMmReplPack.setWhsePackCnt(2);
			ccSpMmReplPack.setReplUnits(4);
			ccSpMmReplPack.setReplenObj(val);
			ccSpMmReplPacks.add(ccSpMmReplPack);
		});

		List<CcMmReplPack> ccMmReplPack = new ArrayList<>();
		CcMmReplPack ccMmReplPack1 = new CcMmReplPack();
		ccMmReplPack1.setCcSpMmReplPack(ccSpMmReplPacks);
		ccMmReplPack.add(ccMmReplPack1);

		replenishmentMapper.updateVnpkWhpkForCcMmReplnPkConsMapper(ccMmReplPack,12,2);
		verify(ccSpReplnPkConsRepository,Mockito.times(1)).saveAll(ccSpMmReplPackArgumentCaptor.capture());
		assertEquals(1,ccSpMmReplPackArgumentCaptor.getAllValues().size());
		List<CcSpMmReplPack> allCcSpMmReplPackValues = ccSpMmReplPackArgumentCaptor.getAllValues().iterator().next();
		for(CcSpMmReplPack ccSpMmReplPack:allCcSpMmReplPackValues){
			Replenishment[] replenishments= objectMapper.readValue(ccSpMmReplPack.getReplenObj(),Replenishment[].class);
			for(Replenishment replenishment:replenishments){
				if(replenishment.getAdjReplnUnits() > 0) {
					assertEquals(0, replenishment.getAdjReplnUnits() % 12);
					assertTrue(replenishment.getAdjReplnUnits() > 500);
				}
			}
		}

	}

	private CcSpMmReplPackId getCcSpMmReplPackId(Integer channelId) {
		MerchCatgReplPackId merchCatgReplPackId = new MerchCatgReplPackId();
		merchCatgReplPackId.setChannelId(channelId);
		merchCatgReplPackId.setPlanId(12L);
		merchCatgReplPackId.setRepTLvl1(34);
		SubCatgReplPackId subCatgReplPackId = new SubCatgReplPackId();
		subCatgReplPackId.setMerchCatgReplPackId(merchCatgReplPackId);
		FinelineReplPackId finelineReplPackId = new FinelineReplPackId();
		finelineReplPackId.setSubCatgReplPackId(subCatgReplPackId);
		StyleReplPackId styleReplPackId = new StyleReplPackId();
		styleReplPackId.setFinelineReplPackId(finelineReplPackId);
		CcReplPackId ccReplPackId = new CcReplPackId();
		ccReplPackId.setStyleReplPackId(styleReplPackId);
		CcMmReplPackId ccMmReplPackId = new CcMmReplPackId();
		ccMmReplPackId.setCcReplPackId(ccReplPackId);
		CcSpMmReplPackId ccSpMmReplPackId = new CcSpMmReplPackId();
		ccSpMmReplPackId.setCcMmReplPackId(ccMmReplPackId);
		return ccSpMmReplPackId;
	}

	private List<Replenishment> getReplenishments(Long replenAdj1, Long replenAdj2) {
		List<Replenishment> replenishments = new ArrayList<>();
		Replenishment replenishments1 = new Replenishment();
		replenishments1.setReplnWeek(1);
		replenishments1.setReplnWeekDesc("test");
		replenishments1.setReplnUnits(1L);
		replenishments1.setAdjReplnUnits(replenAdj1);
		replenishments1.setRemainingUnits(1L);
		replenishments1.setDcInboundUnits(1L);
		replenishments1.setDcInboundAdjUnits(1L);
		replenishments.add(replenishments1);

		Replenishment replenishments2 = new Replenishment();
		replenishments2.setReplnWeek(2);
		replenishments2.setReplnWeekDesc("test");
		replenishments2.setReplnUnits(1L);
		replenishments2.setAdjReplnUnits(replenAdj2);
		replenishments2.setRemainingUnits(1L);
		replenishments2.setDcInboundUnits(1L);
		replenishments2.setDcInboundAdjUnits(1L);
		replenishments.add(replenishments2);
		return replenishments;
	}

}