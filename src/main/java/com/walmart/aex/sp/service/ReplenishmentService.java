package com.walmart.aex.sp.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.entity.CcReplenishmentPack;
import com.walmart.aex.sp.entity.FinelineReplenishmentPack;
import com.walmart.aex.sp.entity.MerchCatgReplenishmentPack;
import com.walmart.aex.sp.entity.StyleReplenishmentPack;
import com.walmart.aex.sp.entity.SubCatgReplenishmentPack;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.SizeListReplenishmentRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceReplenishmentRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;

import lombok.extern.slf4j.Slf4j;
import com.walmart.aex.sp.exception.CustomException;
import java.util.Collection;

@Service
@Slf4j
public class ReplenishmentService  {
    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";

    private final FineLineReplenishmentRepository fineLineReplenishmentRepository;
    private final SpCustomerChoiceReplenishmentRepository  spCustomerChoiceReplenishmentRepository;
    private final SizeListReplenishmentRepository sizeListReplenishmentRepository;
    private final CatgReplnPkConsRepository catgReplnPkConsRepository;
	private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	private final StyleReplnPkConsRepository styleReplnConsRepository;
	private final CcReplnPkConsRepository ccReplnConsRepository;
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
	   Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
	   Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
	   Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
	   Double vnpkwhpkRatio = ((double)vnpk/whpk);
	   List<MerchCatgReplenishmentPack> catgReplnPkConsList = catgReplnPkConsRepository.getCatgReplnConsData(planId, channelId, lvl3Nbr);
	   updateReplnConfigMapper.updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);

	}


	public void updateVnpkWhpkForSubCatgReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
	   Long planId = updateVnPkWhPkReplnRequest.getPlanId();
	   Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
	   Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
	   Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
	   Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
	   Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
	   Double vnpkwhpkRatio = ((double)vnpk/whpk);
	   List<SubCatgReplenishmentPack> SubcatgReplnPkConsList = subCatgReplnPkConsRepository.getSubCatgReplnConsData(planId, channelId, lvl3Nbr,lvl4Nbr);
	   updateReplnConfigMapper.updateVnpkWhpkForSubCatgReplnConsMapper(SubcatgReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);

	}
	public void updateVnpkWhpkForFinelineReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
		Long planId = updateVnPkWhPkReplnRequest.getPlanId();
		Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
		Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
		Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
		Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
		Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
		Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
		Double vnpkwhpkRatio = ((double)vnpk/whpk);
		
		List<FinelineReplenishmentPack> ccReplnPkConsList = finelineReplnPkConsRepository.getCcReplnConsData(planId, channelId, lvl3Nbr, lvl4Nbr, fineline);
		
		updateReplnConfigMapper.updateVnpkWhpkForFinelineReplnConsMapper(ccReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);
		
	}

	
	public void updateVnpkWhpkForStyleReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
        String style = updateVnPkWhPkReplnRequest.getStyle();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Double vnpkwhpkRatio = ((double) vnpk / whpk);

        List<StyleReplenishmentPack> styleReplnPkConsList = styleReplnConsRepository.getCcReplnConsData(planId,
                channelId, lvl3Nbr, lvl4Nbr, fineline, style);

        updateReplnConfigMapper.updateVnpkWhpkForStyleReplnConsMapper(styleReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);
    }
	
	public void updateVnpkWhpkForCcReplnPkCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
		Long planId = updateVnPkWhPkReplnRequest.getPlanId();
		Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
		Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
		Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
		Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
		String style = updateVnPkWhPkReplnRequest.getStyle();
		String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
		Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
		Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
		Double vnpkwhpkRatio = ((double)vnpk/whpk);
		
		List<CcReplenishmentPack> ccReplnPkConsList = ccReplnConsRepository.getCcReplnConsData(planId, channelId, lvl3Nbr, lvl4Nbr, fineline, style, customerChoice);
		
		updateReplnConfigMapper.updateVnpkWhpkForCcReplnPkConsMapper(ccReplnPkConsList, vnpk, whpk, vnpkwhpkRatio);
	}
	
	public void updateVnPkWhPkCcSpMerchMethodReplnCon(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

		Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer lvl3nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer finelineNbr = updateVnPkWhPkReplnRequest.getFineline();
        String stylenbr = updateVnPkWhPkReplnRequest.getStyle();
        String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
        String merchmethodDesc = updateVnPkWhPkReplnRequest.getMerchMethodDesc();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Double vnpkWhpkRatio = ((double) vnpk / whpk);

        ccSpReplnPkConsRepository.updateMerchMethodData(planId, channelId, lvl3nbr, lvl4nbr, finelineNbr, stylenbr,
                customerChoice, vnpk, whpk, vnpkWhpkRatio, merchmethodDesc);
    }
	
	public void updateVnPkWhPkCcSpSizeReplnCon(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer lvl3nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer finelineNbr = updateVnPkWhPkReplnRequest.getFineline();
        String stylenbr = updateVnPkWhPkReplnRequest.getStyle();
        String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
        Integer ahsSizeId=updateVnPkWhPkReplnRequest.getAhsSizeId();
        String merchmethodDesc = updateVnPkWhPkReplnRequest.getMerchMethodDesc();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();
        Double vnpkWhpkRatio = ((double) vnpk / whpk);

        ccSpReplnPkConsRepository.updateSizeData(planId, channelId, lvl3nbr, lvl4nbr, finelineNbr, stylenbr,
                customerChoice,ahsSizeId, vnpk, whpk, vnpkWhpkRatio, merchmethodDesc);
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
}



