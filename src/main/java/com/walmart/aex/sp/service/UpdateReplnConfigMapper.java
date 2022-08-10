package com.walmart.aex.sp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UpdateReplnConfigMapper {

	private final CatgReplnPkConsRepository catgReplnPkConsRepository;
	private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	private final StyleReplnPkConsRepository styleReplnConsRepository;
	private final CcReplnPkConsRepository ccReplnPkConsRepository;
	private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
	private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
	public UpdateReplnConfigMapper(CatgReplnPkConsRepository catgReplnPkConsRepository, SubCatgReplnPkConsRepository subCatgReplnPkConsRepository,
	                     FinelineReplnPkConsRepository finelineReplnPkConsRepository,
	                     StyleReplnPkConsRepository styleReplnConsRepository, CcReplnPkConsRepository ccReplnPkConsRepository,
	                     CcMmReplnPkConsRepository ccMmReplnPkConsRepository,
	                     CcSpReplnPkConsRepository ccSpReplnPkConsRepository) {
	   this.catgReplnPkConsRepository = catgReplnPkConsRepository;
	   this.subCatgReplnPkConsRepository = subCatgReplnPkConsRepository;
	   this.finelineReplnPkConsRepository = finelineReplnPkConsRepository;
	   this.styleReplnConsRepository = styleReplnConsRepository;
	   this.ccReplnPkConsRepository = ccReplnPkConsRepository;
	   this.ccMmReplnPkConsRepository = ccMmReplnPkConsRepository;
	   this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
	}
	
	public void updateVnpkWhpkForCatgReplnConsMapper(List<MerchCatgReplPack> catgReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio, Integer replPackCnt)
	{
	   catgReplnPkConsList.forEach(catgReplnPkCons -> {

	      catgReplnPkCons.setVendorPackCnt(vnpk);
	      catgReplnPkCons.setWhsePackCnt(whpk);
	      catgReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
		  catgReplnPkCons.setReplPackCnt(replPackCnt);
	      catgReplnPkConsRepository.save(catgReplnPkCons);
	   });
	   List<SubCatgReplPack> subCatgReplnPkConsList = catgReplnPkConsList
	         .stream()
	         .flatMap(catgReplnPkCons -> catgReplnPkCons.getSubReplPack().stream()).collect(Collectors.toList());
	   updateVnpkWhpkForSubCatgReplnConsMapper(subCatgReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, replPackCnt);

	}

	public void updateVnpkWhpkForSubCatgReplnConsMapper(List<SubCatgReplPack> SubcatgReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio, Integer replPackCnt)
	{
	   SubcatgReplnPkConsList.forEach(subCatgReplnPkCons -> {

	      subCatgReplnPkCons.setVendorPackCnt(vnpk);
	      subCatgReplnPkCons.setWhsePackCnt(whpk);
	      subCatgReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
		  subCatgReplnPkCons.setReplPackCnt(replPackCnt);
	      subCatgReplnPkConsRepository.save(subCatgReplnPkCons);
	   });
	   List<FinelineReplPack> finelineReplnPkConsList = SubcatgReplnPkConsList
	         .stream()
	         .flatMap(subCatgReplnPkCons -> subCatgReplnPkCons.getFinelineReplPack().stream()).collect(Collectors.toList());
	   updateVnpkWhpkForFinelineReplnConsMapper(finelineReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, replPackCnt);

	}
	
	public void updateVnpkWhpkForFinelineReplnConsMapper(List<FinelineReplPack> finelineReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio, Integer replPackCnt)
	{
		finelineReplnPkConsList.forEach(finelieneReplnPkCons -> {
			
			finelieneReplnPkCons.setVendorPackCnt(vnpk);
			finelieneReplnPkCons.setWhsePackCnt(whpk);
			finelieneReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			finelieneReplnPkCons.setReplPackCnt(replPackCnt);
			
			finelineReplnPkConsRepository.save(finelieneReplnPkCons);
		});
		
		List<StyleReplPack> stylReplnPkConsList = finelineReplnPkConsList
				        .stream()
						.flatMap(finelieneReplnPkCons -> finelieneReplnPkCons.getStyleReplPack().stream()).collect(Collectors.toList());	
						
		updateVnpkWhpkForStyleReplnConsMapper(stylReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, replPackCnt);
		
	}

	
	public void updateVnpkWhpkForStyleReplnConsMapper(List<StyleReplPack> styleReplnPkConsList, Integer vnpk,
            Integer whpk, Double vnpkwhpkRatio, Integer replPackCnt) {
        styleReplnPkConsList.forEach(styleReplnPkCons -> {

            styleReplnPkCons.setVendorPackCnt(vnpk);
            styleReplnPkCons.setWhsePackCnt(whpk);
            styleReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			styleReplnPkCons.setReplPackCnt(replPackCnt);

            styleReplnConsRepository.save(styleReplnPkCons);
        });

        List<CcReplPack> ccReplnPkConsList = styleReplnPkConsList.stream()
                .flatMap(styleReplnPkCons -> styleReplnPkCons.getCcReplPack().stream())
                .collect(Collectors.toList());

        updateVnpkWhpkForCcReplnPkConsMapper(ccReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, replPackCnt);

    }
	
	public void updateVnpkWhpkForCcReplnPkConsMapper(List<CcReplPack> ccReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio, Integer replPackCnt)
	{
		ccReplnPkConsList.forEach(ccReplnPkCons -> {
			
			ccReplnPkCons.setVendorPackCnt(vnpk);
			ccReplnPkCons.setWhsePackCnt(whpk);
			ccReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			ccReplnPkCons.setReplPackCnt(replPackCnt);
			
			ccReplnPkConsRepository.save(ccReplnPkCons);
		});
		
		List<CcMmReplPack> ccMmReplnPkConsList = ccReplnPkConsList.stream().
						flatMap(ccReplnPkCons -> ccReplnPkCons.getCcMmReplPack().stream()).collect(Collectors.toList());
		
		updateVnpkWhpkForCcMmReplnPkConsMapper(ccMmReplnPkConsList, vnpk, whpk, vnpkwhpkRatio,replPackCnt);
		
	}
	
	public void updateVnpkWhpkForCcMmReplnPkConsMapper(List<CcMmReplPack> ccMmReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio, Integer replPackCnt)
	{
		ccMmReplnPkConsList.forEach(ccMmReplnPkCons -> {

			ccMmReplnPkCons.setVendorPackCnt(vnpk);
			ccMmReplnPkCons.setWhsePackCnt(whpk);
			ccMmReplnPkCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			ccMmReplnPkCons.setReplPackCnt(replPackCnt);

			ccMmReplnPkConsRepository.save(ccMmReplnPkCons);
		});

		List<CcSpMmReplPack> ccSpMmReplnPkConsList = ccMmReplnPkConsList.stream().
						flatMap(ccMmReplnPkCons -> ccMmReplnPkCons.getCcSpMmReplPack().stream()).collect(Collectors.toList());

		updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, replPackCnt);

	}
	
	public void updateVnpkWhpkForCcSpMmReplnPkConsMapper(List<CcSpMmReplPack> ccSpReplnPkConsList, Integer vnpk, Integer whpk, Double vnpkwhpkRatio, Integer replPackCnt)
	{
		ccSpReplnPkConsList.forEach(ccSpReplnCons -> {
			
			ccSpReplnCons.setVendorPackCnt(vnpk);
			ccSpReplnCons.setWhsePackCnt(whpk);
			ccSpReplnCons.setVnpkWhpkRatio(vnpkwhpkRatio);
			ccSpReplnCons.setReplPackCnt(replPackCnt);
			
			ccSpReplnPkConsRepository.save(ccSpReplnCons);
		});
	}
}
