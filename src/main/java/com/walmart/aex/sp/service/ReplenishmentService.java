package com.walmart.aex.sp.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcReplPack;
import com.walmart.aex.sp.entity.FinelineReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.SubCatgReplPack;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.SizeListReplenishmentRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceReplenishmentRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReplenishmentService  {
    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";
    public static Double vnpkwhpkRatio=0.0;

    private final FineLineReplenishmentRepository fineLineReplenishmentRepository;
    private final SpCustomerChoiceReplenishmentRepository  spCustomerChoiceReplenishmentRepository;
    private final SizeListReplenishmentRepository sizeListReplenishmentRepository;
    private final CatgReplnPkConsRepository catgReplnPkConsRepository;
	private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	private final StyleReplnPkConsRepository styleReplnConsRepository;
	private final CcReplnPkConsRepository ccReplnConsRepository;
	private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
	private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    private final ReplenishmentMapper replenishmentMapper;
    private final UpdateReplnConfigMapper updateReplnConfigMapper;

    public ReplenishmentService(FineLineReplenishmentRepository fineLineReplenishmentRepository,
                                SpCustomerChoiceReplenishmentRepository  spCustomerChoiceReplenishmentRepository,
                                SizeListReplenishmentRepository sizeListReplenishmentRepository,
                                CatgReplnPkConsRepository catgReplnPkConsRepository,
                                SubCatgReplnPkConsRepository subCatgReplnPkConsRepository,
                                FinelineReplnPkConsRepository finelineReplnPkConsRepository,
                                StyleReplnPkConsRepository styleReplnConsRepository,
                                CcReplnPkConsRepository ccReplnConsRepository,
                                CcMmReplnPkConsRepository ccMmReplnPkConsRepository,
                                CcSpReplnPkConsRepository ccSpReplnPkConsRepository,
                                ReplenishmentMapper replenishmentMapper,
                                UpdateReplnConfigMapper updateReplnConfigMapper) {
        this.fineLineReplenishmentRepository = fineLineReplenishmentRepository;
        this.spCustomerChoiceReplenishmentRepository=spCustomerChoiceReplenishmentRepository;
        this.sizeListReplenishmentRepository=sizeListReplenishmentRepository;
        this.catgReplnPkConsRepository = catgReplnPkConsRepository;
        this.subCatgReplnPkConsRepository = subCatgReplnPkConsRepository;
        this.finelineReplnPkConsRepository = finelineReplnPkConsRepository;
        this.styleReplnConsRepository = styleReplnConsRepository;
        this.ccReplnConsRepository = ccReplnConsRepository;
        this.ccMmReplnPkConsRepository = ccMmReplnPkConsRepository;
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
        this.replenishmentMapper = replenishmentMapper;
        this.updateReplnConfigMapper = updateReplnConfigMapper;
    }

    public ReplenishmentResponse fetchFinelineReplenishment(ReplenishmentRequest replenishmentRequest) {
        ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
        Integer channelId=ChannelType.getChannelIdFromName(replenishmentRequest.getChannel());

        try {
            List<ReplenishmentResponseDTO> replenishmentResponseDTOS = fineLineReplenishmentRepository
                    .getByPlanChannel(replenishmentRequest.getPlanId(),channelId );
            Optional.of(replenishmentResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ReplenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(ReplenishmentResponseDTO, replenishmentResponse, null,null));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline Replenishment :", e);
            throw new CustomException("Failed to fetch Fineline Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment Fineline response: {}", replenishmentResponse);
        return replenishmentResponse;
    }

    public ReplenishmentResponse fetchCcReplenishment(ReplenishmentRequest replenishmentRequest) {
        ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
        Integer finelineNbr = replenishmentRequest.getFinelineNbr();
        try {
            List<ReplenishmentResponseDTO> replenishmentResponseDTOS = spCustomerChoiceReplenishmentRepository
                    .getReplenishmentByPlanChannelFineline(replenishmentRequest.getPlanId(),
                            ChannelType.getChannelIdFromName(replenishmentRequest.getChannel()),replenishmentRequest.getFinelineNbr());
            Optional.of(replenishmentResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ReplenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(ReplenishmentResponseDTO, replenishmentResponse,finelineNbr,null));
        } catch (Exception e) {
            log.error("Exception While fetching style and CC Replenishment :", e);
            throw new CustomException("Failed to fetch style and CC Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment style and CC response: {}", replenishmentResponse);
        return replenishmentResponse;
    }


    public ReplenishmentResponse fetchSizeListReplenishment(ReplenishmentRequest replenishmentRequest) {
        ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
        String ccId=replenishmentRequest.getCcId();
        String styleNbr= replenishmentRequest.getStyleNbr();
        Integer finelineNbr = replenishmentRequest.getFinelineNbr();
        try {
            List<ReplenishmentResponseDTO> replenishmentResponseDTOS = sizeListReplenishmentRepository
                    .getReplenishmentPlanChannelFinelineCc(replenishmentRequest.getPlanId(), ChannelType.getChannelIdFromName(replenishmentRequest.getChannel()),
                            finelineNbr,styleNbr,ccId);
            Optional.of(replenishmentResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ReplenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(ReplenishmentResponseDTO, replenishmentResponse,finelineNbr,ccId));
        } catch (Exception e) {
            log.error("Exception While fetching MerchMethod Replenishment :", e);
            throw new CustomException("Failed to fetch MerchMethod Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment MerchMethod response: {}", replenishmentResponse);
        return replenishmentResponse;
    }
    
    public void updateVnpkWhpkForCatgReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
	   Long planId = updateVnPkWhPkReplnRequest.getPlanId();
	   Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
	   Integer fixtureTypeRollupId = updateVnPkWhPkReplnRequest.getFixtureTypeRollupId();
	   Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
	   Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
	   Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
       Integer replenishmentUnits = updateVnPkWhPkReplnRequest.getRepleshUnits();
	   
	   vnpkwhpkRatio = getVnpkWhpkRatio(vnpk, whpk);
	   
	   List<MerchCatgReplPack> catgReplnPkConsList = catgReplnPkConsRepository.getCatgReplnConsData(planId, channelId, lvl3Nbr, fixtureTypeRollupId);
	   System.out.println(catgReplnPkConsList);
	   updateReplnConfigMapper.updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, getReplenishmentPackCount(replenishmentUnits, vnpk));

	}


	public void updateVnpkWhpkForSubCatgReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
	   Long planId = updateVnPkWhPkReplnRequest.getPlanId();
	   Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
	   Integer fixtureTypeRollupId = updateVnPkWhPkReplnRequest.getFixtureTypeRollupId();
	   Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
	   Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
	   Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
	   Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
       Integer replenishmentUnits = updateVnPkWhPkReplnRequest.getRepleshUnits();

	   vnpkwhpkRatio = getVnpkWhpkRatio(vnpk, whpk);
	   
	   List<SubCatgReplPack> SubcatgReplnPkConsList = subCatgReplnPkConsRepository.getSubCatgReplnConsData(planId, channelId, lvl3Nbr,lvl4Nbr, fixtureTypeRollupId);
	   updateReplnConfigMapper.updateVnpkWhpkForSubCatgReplnConsMapper(SubcatgReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, getReplenishmentPackCount(replenishmentUnits, vnpk));

	}
	public void updateVnpkWhpkForFinelineReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
		Long planId = updateVnPkWhPkReplnRequest.getPlanId();
		Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
		Integer fixtureTypeRollupId = updateVnPkWhPkReplnRequest.getFixtureTypeRollupId();
		Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
		Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
		Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
		Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
		Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Integer replenishmentUnits = updateVnPkWhPkReplnRequest.getRepleshUnits();
		
		vnpkwhpkRatio = getVnpkWhpkRatio(vnpk, whpk);
		
		List<FinelineReplPack> ccReplnPkConsList = finelineReplnPkConsRepository.getFinelineReplnConsData(planId, channelId, lvl3Nbr, lvl4Nbr, fineline, fixtureTypeRollupId);
		
		updateReplnConfigMapper.updateVnpkWhpkForFinelineReplnConsMapper(ccReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, getReplenishmentPackCount(replenishmentUnits, vnpk));
		
	}

	
	public void updateVnpkWhpkForStyleReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer fixtureTypeRollupId = updateVnPkWhPkReplnRequest.getFixtureTypeRollupId();
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
        String style = updateVnPkWhPkReplnRequest.getStyle();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Integer replenishmentUnits = updateVnPkWhPkReplnRequest.getRepleshUnits();

        vnpkwhpkRatio = getVnpkWhpkRatio(vnpk, whpk);

        List<StyleReplPack> styleReplnPkConsList = styleReplnConsRepository.getStyleReplnConsData(planId,
                channelId, lvl3Nbr, lvl4Nbr, fineline, style, fixtureTypeRollupId);

        updateReplnConfigMapper.updateVnpkWhpkForStyleReplnConsMapper(styleReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, getReplenishmentPackCount(replenishmentUnits, vnpk));
    }
	
	public void updateVnpkWhpkForCcReplnPkCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
		Long planId = updateVnPkWhPkReplnRequest.getPlanId();
		Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
		Integer fixtureTypeRollupId = updateVnPkWhPkReplnRequest.getFixtureTypeRollupId();
		Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
		Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
		Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
		String style = updateVnPkWhPkReplnRequest.getStyle();
		String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
		Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
		Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Integer replenishmentUnits = updateVnPkWhPkReplnRequest.getRepleshUnits();

		vnpkwhpkRatio = getVnpkWhpkRatio(vnpk, whpk);
		
		List<CcReplPack> ccReplnPkConsList = ccReplnConsRepository.getCcReplnConsData(planId, channelId, lvl3Nbr, lvl4Nbr, fineline, style, customerChoice, fixtureTypeRollupId);
		
		updateReplnConfigMapper.updateVnpkWhpkForCcReplnPkConsMapper(ccReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, getReplenishmentPackCount(replenishmentUnits, vnpk));
	}
	
	public void updateVnPkWhPkCcMerchMethodReplnCon(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

		Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer fixtureTypeRollupId = updateVnPkWhPkReplnRequest.getFixtureTypeRollupId();
        Integer lvl3nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer finelineNbr = updateVnPkWhPkReplnRequest.getFineline();
        String stylenbr = updateVnPkWhPkReplnRequest.getStyle();
        String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
        String merchmethodDesc = updateVnPkWhPkReplnRequest.getMerchMethodDesc();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Integer replenishmentUnits = updateVnPkWhPkReplnRequest.getRepleshUnits();

        vnpkwhpkRatio = getVnpkWhpkRatio(vnpk, whpk);

        List<CcMmReplPack> ccMmReplnPkConsList = ccMmReplnPkConsRepository.getCcMmReplnPkConsData(planId, channelId, lvl3nbr, lvl4nbr, finelineNbr, stylenbr,
                customerChoice, merchmethodDesc, fixtureTypeRollupId);
        
        updateReplnConfigMapper.updateVnpkWhpkForCcMmReplnPkConsMapper(ccMmReplnPkConsList, vnpk, whpk, vnpkwhpkRatio, getReplenishmentPackCount(replenishmentUnits, vnpk));
    }
	
	
	public void updateVnPkWhPkCcSpSizeReplnCon(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer fixtureTypeRollupId = updateVnPkWhPkReplnRequest.getFixtureTypeRollupId();
        Integer lvl3nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer finelineNbr = updateVnPkWhPkReplnRequest.getFineline();
        String stylenbr = updateVnPkWhPkReplnRequest.getStyle();
        String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
        Integer ahsSizeId=updateVnPkWhPkReplnRequest.getAhsSizeId();
        String merchmethodDesc = updateVnPkWhPkReplnRequest.getMerchMethodDesc();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Integer replenishmentUnits = updateVnPkWhPkReplnRequest.getRepleshUnits();

        vnpkwhpkRatio = getVnpkWhpkRatio(vnpk, whpk);

        ccSpReplnPkConsRepository.updateSizeData(planId, channelId, lvl3nbr, lvl4nbr, finelineNbr, stylenbr,
                customerChoice,ahsSizeId, vnpk, whpk, vnpkwhpkRatio, getReplenishmentPackCount(replenishmentUnits, vnpk), merchmethodDesc, fixtureTypeRollupId);
    }
	
	private Integer getChannelId(String channel)
	{
		Integer channelId = 0;
		
		if("Store".equalsIgnoreCase(channel))
			channelId = 1;
		else if("Online".equalsIgnoreCase(channel))
			channelId = 2;
		
		return channelId;
				
	}
	
	private Double getVnpkWhpkRatio(Integer vnpk, Integer whpk)
	{
		Double vnwhpkRatio = null;
		
		if(vnpk!=0 && whpk!=0) {
			vnwhpkRatio = ((double) vnpk / whpk);
	  	   }
		
		return vnwhpkRatio;
		
	}

    private Integer getReplenishmentPackCount(Integer replenishmentUnits, Integer vnpk )
    {
        Integer ReplenishmentPackCount = null;
        if(replenishmentUnits!= null && vnpk!= null){
            ReplenishmentPackCount =  ((int) replenishmentUnits/vnpk);
        }
        return  ReplenishmentPackCount;
    }
	
}



