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

	private Integer replPackCnt = 0;
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

	public void updateVnpkWhpkForCatgReplnConsMapper(List<MerchCatgReplPack> catgReplnPkConsList, Integer vnpk, Integer whpk)
	{
	   catgReplnPkConsList.forEach(catgReplnPkCons -> {
		  
		   if(vnpk != null)
			   catgReplnPkCons.setVendorPackCnt(vnpk);
	      
		   if(whpk != null)
			   catgReplnPkCons.setWhsePackCnt(whpk);
	      
	      catgReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(catgReplnPkCons.getVendorPackCnt(), catgReplnPkCons.getWhsePackCnt()));
	      replPackCnt = getReplenishmentPackCount(catgReplnPkCons.getReplUnits(), catgReplnPkCons.getVendorPackCnt());
	      
		  catgReplnPkCons.setReplPackCnt(replPackCnt);
	      catgReplnPkConsRepository.save(catgReplnPkCons);
	   });
	   List<SubCatgReplPack> subCatgReplnPkConsList = catgReplnPkConsList
	         .stream()
	         .flatMap(catgReplnPkCons -> catgReplnPkCons.getSubReplPack().stream()).collect(Collectors.toList());
	   updateVnpkWhpkForSubCatgReplnConsMapper(subCatgReplnPkConsList, vnpk, whpk);

	}

	public void updateVnpkWhpkForSubCatgReplnConsMapper(List<SubCatgReplPack> SubcatgReplnPkConsList, Integer vnpk, Integer whpk)
	{
	   SubcatgReplnPkConsList.forEach(subCatgReplnPkCons -> {

		   if(vnpk != null)
			   subCatgReplnPkCons.setVendorPackCnt(vnpk);
	      
		   if(whpk != null)
			   subCatgReplnPkCons.setWhsePackCnt(whpk);
	      
	      subCatgReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(subCatgReplnPkCons.getVendorPackCnt(), subCatgReplnPkCons.getWhsePackCnt()));
	      replPackCnt = getReplenishmentPackCount(subCatgReplnPkCons.getReplUnits(), subCatgReplnPkCons.getVendorPackCnt());
	      
		  subCatgReplnPkCons.setReplPackCnt(replPackCnt);
	      subCatgReplnPkConsRepository.save(subCatgReplnPkCons);
	   });
	   List<FinelineReplPack> finelineReplnPkConsList = SubcatgReplnPkConsList
	         .stream()
	         .flatMap(subCatgReplnPkCons -> subCatgReplnPkCons.getFinelineReplPack().stream()).collect(Collectors.toList());
	   updateVnpkWhpkForFinelineReplnConsMapper(finelineReplnPkConsList, vnpk, whpk);

	}
	
	public void updateVnpkWhpkForFinelineReplnConsMapper(List<FinelineReplPack> finelineReplnPkConsList, Integer vnpk, Integer whpk)
	{
		finelineReplnPkConsList.forEach(finelieneReplnPkCons -> {
			
			if(vnpk != null)
				finelieneReplnPkCons.setVendorPackCnt(vnpk);
		      
			if(whpk != null)
				finelieneReplnPkCons.setWhsePackCnt(whpk);
			
			finelieneReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(finelieneReplnPkCons.getVendorPackCnt(), finelieneReplnPkCons.getWhsePackCnt()));
			replPackCnt = getReplenishmentPackCount(finelieneReplnPkCons.getReplUnits(), finelieneReplnPkCons.getVendorPackCnt());
			
			finelieneReplnPkCons.setReplPackCnt(replPackCnt);
			
			finelineReplnPkConsRepository.save(finelieneReplnPkCons);
		});
		
		List<StyleReplPack> stylReplnPkConsList = finelineReplnPkConsList
				        .stream()
						.flatMap(finelieneReplnPkCons -> finelieneReplnPkCons.getStyleReplPack().stream()).collect(Collectors.toList());	
						
		updateVnpkWhpkForStyleReplnConsMapper(stylReplnPkConsList, vnpk, whpk);
		
	}

	
	public void updateVnpkWhpkForStyleReplnConsMapper(List<StyleReplPack> styleReplnPkConsList, Integer vnpk,
            Integer whpk) {
        styleReplnPkConsList.forEach(styleReplnPkCons -> {

			if(vnpk != null)
				styleReplnPkCons.setVendorPackCnt(vnpk);
		      
			if(whpk != null)
			{
				styleReplnPkCons.setWhsePackCnt(whpk);
			}

        	styleReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(styleReplnPkCons.getVendorPackCnt(), styleReplnPkCons.getWhsePackCnt()));
            replPackCnt = getReplenishmentPackCount(styleReplnPkCons.getReplUnits(), styleReplnPkCons.getVendorPackCnt());
            
			styleReplnPkCons.setReplPackCnt(replPackCnt);

            styleReplnConsRepository.save(styleReplnPkCons);
        });

        List<CcReplPack> ccReplnPkConsList = styleReplnPkConsList.stream()
                .flatMap(styleReplnPkCons -> styleReplnPkCons.getCcReplPack().stream())
                .collect(Collectors.toList());

        updateVnpkWhpkForCcReplnPkConsMapper(ccReplnPkConsList, vnpk, whpk);

    }
	
	public void updateVnpkWhpkForCcReplnPkConsMapper(List<CcReplPack> ccReplnPkConsList, Integer vnpk, Integer whpk)
	{
		ccReplnPkConsList.forEach(ccReplnPkCons -> {
			
			if(vnpk != null)
				ccReplnPkCons.setVendorPackCnt(vnpk);
		      
			if(whpk != null)
				ccReplnPkCons.setWhsePackCnt(whpk);
			
			ccReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(ccReplnPkCons.getVendorPackCnt(), ccReplnPkCons.getWhsePackCnt()));
			replPackCnt = getReplenishmentPackCount(ccReplnPkCons.getReplUnits(), ccReplnPkCons.getVendorPackCnt());
			
			ccReplnPkCons.setReplPackCnt(replPackCnt);
			
			ccReplnPkConsRepository.save(ccReplnPkCons);
		});
		
		List<CcMmReplPack> ccMmReplnPkConsList = ccReplnPkConsList.stream().
						flatMap(ccReplnPkCons -> ccReplnPkCons.getCcMmReplPack().stream()).collect(Collectors.toList());
		
		updateVnpkWhpkForCcMmReplnPkConsMapper(ccMmReplnPkConsList, vnpk, whpk);
		
	}
	
	public void updateVnpkWhpkForCcMmReplnPkConsMapper(List<CcMmReplPack> ccMmReplnPkConsList, Integer vnpk, Integer whpk)
	{
		ccMmReplnPkConsList.forEach(ccMmReplnPkCons -> {

			if(vnpk != null)
				ccMmReplnPkCons.setVendorPackCnt(vnpk);
		      
			if(whpk != null)
				ccMmReplnPkCons.setWhsePackCnt(whpk);

			ccMmReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(ccMmReplnPkCons.getVendorPackCnt(), ccMmReplnPkCons.getWhsePackCnt()));
			replPackCnt = getReplenishmentPackCount(ccMmReplnPkCons.getReplUnits(), ccMmReplnPkCons.getVendorPackCnt());
			
			ccMmReplnPkCons.setReplPackCnt(replPackCnt);

			ccMmReplnPkConsRepository.save(ccMmReplnPkCons);
		});

		List<CcSpMmReplPack> ccSpMmReplnPkConsList = ccMmReplnPkConsList.stream().
						flatMap(ccMmReplnPkCons -> ccMmReplnPkCons.getCcSpMmReplPack().stream()).collect(Collectors.toList());

		updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplnPkConsList, vnpk, whpk);

	}

	public void updateVnpkWhpkForCcSpMmReplnPkConsMapper(List<CcSpMmReplPack> ccSpReplnPkConsList, Integer vnpk, Integer whpk)
	{
		ccSpReplnPkConsList.forEach(ccSpReplnCons -> {
			
			if(vnpk != null)
				ccSpReplnCons.setVendorPackCnt(vnpk);
		      
			if(whpk != null)
				ccSpReplnCons.setWhsePackCnt(whpk);
			
			ccSpReplnCons.setVnpkWhpkRatio(getVnpkWhpkRatio(ccSpReplnCons.getVendorPackCnt(), ccSpReplnCons.getWhsePackCnt()));
			replPackCnt = getReplenishmentPackCount(ccSpReplnCons.getReplUnits(), ccSpReplnCons.getVendorPackCnt());
			
			ccSpReplnCons.setReplPackCnt(replPackCnt);
			
			ccSpReplnPkConsRepository.save(ccSpReplnCons);
		});
	}
	
	private Integer getReplenishmentPackCount(Integer replenishmentUnits, Integer vnpk )
    {
        Integer ReplenishmentPackCount = null;
        if(replenishmentUnits!= null && replenishmentUnits!= 0 && vnpk!= null && vnpk!= 0){
            ReplenishmentPackCount =  ((int) replenishmentUnits/vnpk);
        }
        return ReplenishmentPackCount;
    }

	private Double getVnpkWhpkRatio(Integer vnpk, Integer whpk)
    {
        Double vnwhpkRatio = null;

        if(vnpk!=0 && whpk!=0) {
            vnwhpkRatio = ((double) vnpk / whpk);
        }

        return vnwhpkRatio;

    }
}
