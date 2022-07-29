package com.walmart.aex.sp.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.SizeListReplenishmentRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceReplenishmentRepository;
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

    private final ReplenishmentMapper replenishmentMapper;

    public ReplenishmentService(FineLineReplenishmentRepository fineLineReplenishmentRepository,
                                SpCustomerChoiceReplenishmentRepository  spCustomerChoiceReplenishmentRepository,
                                SizeListReplenishmentRepository sizeListReplenishmentRepository,
                                ReplenishmentMapper replenishmentMapper) {
        this.fineLineReplenishmentRepository = fineLineReplenishmentRepository;
        this.spCustomerChoiceReplenishmentRepository=spCustomerChoiceReplenishmentRepository;
        this.sizeListReplenishmentRepository=sizeListReplenishmentRepository;
        this.replenishmentMapper = replenishmentMapper;
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
}



