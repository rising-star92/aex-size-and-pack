package com.walmart.aex.sp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.entity.CcReplenishmentPack;
import com.walmart.aex.sp.entity.CcSpReplenishmentPack;
import com.walmart.aex.sp.entity.FinelineReplenishmentPack;
import com.walmart.aex.sp.entity.MerchCatgReplenishmentPack;
import com.walmart.aex.sp.entity.StyleReplenishmentPack;
import com.walmart.aex.sp.entity.SubCatgReplenishmentPack;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UpdateReplnConfigMapper {

	private final CatgReplnPkConsRepository catgReplnPkConsRepository;
	private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	private final StyleReplnPkConsRepository styleReplnConsRepository;
	private final CcReplnPkConsRepository ccReplnPkConsRepository;
	private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
	public UpdateReplnConfigMapper(CatgReplnPkConsRepository catgReplnPkConsRepository, SubCatgReplnPkConsRepository subCatgReplnPkConsRepository,
	                     FinelineReplnPkConsRepository finelineReplnPkConsRepository,
	                     StyleReplnPkConsRepository styleReplnConsRepository, CcReplnPkConsRepository ccReplnPkConsRepository,
	                     CcSpReplnPkConsRepository ccSpReplnPkConsRepository) {
	   this.catgReplnPkConsRepository = catgReplnPkConsRepository;
	   this.subCatgReplnPkConsRepository = subCatgReplnPkConsRepository;
	   this.finelineReplnPkConsRepository = finelineReplnPkConsRepository;
	   this.styleReplnConsRepository = styleReplnConsRepository;
	   this.ccReplnPkConsRepository = ccReplnPkConsRepository;
	   this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
	}
	
	public void updateVnpkWhpkForCatgReplnConsMapper(List<MerchCatgReplenishmentPack> catgReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio)
	{
	   catgReplnPkConsList.forEach(catgReplnPkCons -> {

	      catgReplnPkCons.setVendorPackCnt(vnpk);
	      catgReplnPkCons.setWhsePackCnt(whpk);
	      catgReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
	      catgReplnPkConsRepository.save(catgReplnPkCons);
	   });
	   List<SubCatgReplenishmentPack> subCatgReplnPkConsList = catgReplnPkConsList
	         .stream()
	         .flatMap(finelieneReplnPkCons -> finelieneReplnPkCons.getSubReplenishmentPack().stream()).collect(Collectors.toList());
	   updateVnpkWhpkForSubCatgReplnConsMapper(subCatgReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);

	}

	public void updateVnpkWhpkForSubCatgReplnConsMapper(List<SubCatgReplenishmentPack> SubcatgReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio)
	{
	   SubcatgReplnPkConsList.forEach(subCatgReplnPkCons -> {

	      subCatgReplnPkCons.setVendorPackCnt(vnpk);
	      subCatgReplnPkCons.setWhsePackCnt(whpk);
	      subCatgReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
	      subCatgReplnPkConsRepository.save(subCatgReplnPkCons);
	   });
	   List<FinelineReplenishmentPack> finelineReplnPkConsList = SubcatgReplnPkConsList
	         .stream()
	         .flatMap(subCatgReplnPkCons -> subCatgReplnPkCons.getFinelineReplenishmentPack().stream()).collect(Collectors.toList());
	   updateVnpkWhpkForFinelineReplnConsMapper(finelineReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);

	}
	
	public void updateVnpkWhpkForFinelineReplnConsMapper(List<FinelineReplenishmentPack> finelineReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio)
	{
		finelineReplnPkConsList.forEach(finelieneReplnPkCons -> {
			
			finelieneReplnPkCons.setVendorPackCnt(vnpk);
			finelieneReplnPkCons.setWhsePackCnt(whpk);
			finelieneReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			
			finelineReplnPkConsRepository.save(finelieneReplnPkCons);
		});
		
		List<StyleReplenishmentPack> stylReplnPkConsList = finelineReplnPkConsList
				        .stream()
						.flatMap(finelieneReplnPkCons -> finelieneReplnPkCons.getStyleReplenishmentPack().stream()).collect(Collectors.toList());	
						
		updateVnpkWhpkForStyleReplnConsMapper(stylReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);
		
	}

	
	public void updateVnpkWhpkForStyleReplnConsMapper(List<StyleReplenishmentPack> styleReplnPkConsList, Integer vnpk,
            Integer whpk, Double vnpkwhpkRatio) {
        styleReplnPkConsList.forEach(styleReplnPkCons -> {

            styleReplnPkCons.setVendorPackCnt(vnpk);
            styleReplnPkCons.setWhsePackCnt(whpk);
            styleReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);

            styleReplnConsRepository.save(styleReplnPkCons);
        });

        List<CcReplenishmentPack> ccReplnPkConsList = styleReplnPkConsList.stream()
                .flatMap(styleReplnPkCons -> styleReplnPkCons.getCcReplenishmentPack().stream())
                .collect(Collectors.toList());

        updateVnpkWhpkForCcReplnPkConsMapper(ccReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);

    }
	
	public void updateVnpkWhpkForCcReplnPkConsMapper(List<CcReplenishmentPack> ccReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio)
	{
		ccReplnPkConsList.forEach(ccReplnPkCons -> {
			
			ccReplnPkCons.setVendorPackCnt(vnpk);
			ccReplnPkCons.setWhsePackCnt(whpk);
			ccReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			
			ccReplnPkConsRepository.save(ccReplnPkCons);
		});
		
		List<CcSpReplenishmentPack> ccSpReplnPkConsList = ccReplnPkConsList.stream().
						flatMap(ccReplnPkCons -> ccReplnPkCons.getCcSpReplenishmentPack().stream()).collect(Collectors.toList());
		
		updateVnpkWhpkForCcSpReplnPkConsMapper(ccSpReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);
		
	}
	
	public void updateVnpkWhpkForCcSpReplnPkConsMapper(List<CcSpReplenishmentPack> ccSpReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio)
	{
		ccSpReplnPkConsList.forEach(ccSpReplnCons -> {
			
			ccSpReplnCons.setVendorPackCnt(vnpk);
			ccSpReplnCons.setWhsePackCnt(whpk);
			ccSpReplnCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			
			ccSpReplnPkConsRepository.save(ccSpReplnCons);
		});
	}
}
