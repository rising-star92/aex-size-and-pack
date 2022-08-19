package com.walmart.aex.sp.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.FinelineReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.StyleReplenishmentPack;
import com.walmart.aex.sp.entity.SubCatgReplPack;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;

@RunWith(MockitoJUnitRunner.class)
public class UpdateReplnConfigMapperTest {
	
	private static final Integer vnpk=500;
	private static final Integer whpk=500;
	Double vnpkwhpkRatio=1d;
	
	@InjectMocks
	@Spy
	UpdateReplnConfigMapper replenishmentMapper;
		
	@Mock
	StyleReplnPkConsRepository styleReplnConsRepository;
	
	@Mock
	List<FinelineReplPack> finelineReplnPkConsList;
	
	@Mock
	List<StyleReplenishmentPack> styleReplnPkConsList;
	
	@Mock
	FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	
	@Mock
	CcReplnPkConsRepository ccReplnConsRepository;
		
	@Mock
	List<MerchCatgReplPack> catgReplnPkConsList;
	
	@Mock
	CatgReplnPkConsRepository catgReplnPkConsRepository;
	
	@Mock
	List<SubCatgReplPack> SubcatgReplnPkConsList;
	
	@Mock
	SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	
	@Mock
	CcMmReplnPkConsRepository ccMmRepln;
	
	@Mock
	List<CcSpMmReplPack> ccSpReplnPkConsList1;
	
	@Test
	public void testUpdateVnpkWhpkForStyleReplnConsMapper() {
		
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
					
		replenishmentMapper.updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, 500, 500, vnpkwhpkRatio);
		
		//Assert
    	Mockito.verify(replenishmentMapper,Mockito.times(1)).updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);
    	
    	assertEquals(catgReplnPkConsList.get(0).getVendorPackCnt(), 500);    	
    	assertEquals(catgReplnPkConsList.get(0).getWhsePackCnt(), 500);
    	assertEquals(catgReplnPkConsList.get(0).getVnpkWhpkRatio(), 1d);
    			
	}
}