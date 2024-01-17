package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextRequest;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.dto.mapper.AppMessageTextMapper;
import com.walmart.aex.sp.entity.AppMessageText;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.AppMessageTextRepository;
import com.walmart.aex.sp.service.AppMessageTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AppMessageTextServiceImpl implements AppMessageTextService {

    private final AppMessageTextRepository appMessageTextRepository;

    public AppMessageTextServiceImpl(AppMessageTextRepository appMessageTextRepository) {
        this.appMessageTextRepository = appMessageTextRepository;
    }

    @Override
    @Cacheable("appMessages")
    public List<AppMessageTextResponse> getAllAppMessageText() {
        try {
            List<AppMessageText> appMessageTexts = appMessageTextRepository.findAll();
            return AppMessageTextMapper.mapper.mapEntityToResponse(appMessageTexts);
        } catch (Exception ex) {
            log.error("Error while getting list of App messages. Exception: {}", ex.getMessage());
            throw new CustomException("Exception occurred while retrieving app messages");
        }

    }

    @Override
    @CacheEvict(value = "appMessages", allEntries = true)
    public void addAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests) {
        try {
            if (!CollectionUtils.isEmpty(appMessageTextRequests)) {
                List<AppMessageText> appMessageTexts = AppMessageTextMapper.mapper.mapRequestToEntity(appMessageTextRequests);
                appMessageTextRepository.saveAll(appMessageTexts);
            }
        } catch (Exception ex) {
            log.error("Failed to add new app message. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to add app message");
        }
    }

    @Override
    @CacheEvict(value = "appMessages", allEntries = true)
    public void updateAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests) {
        try {
            List<AppMessageText> updatedRecords = new ArrayList<>();
            if (!CollectionUtils.isEmpty(appMessageTextRequests)) {
                List<AppMessageText> appMessageTexts = AppMessageTextMapper.mapper.mapRequestToEntity(appMessageTextRequests);
                List<AppMessageText> appMessageTextsFromDb = appMessageTextRepository.findAllById(appMessageTexts.stream().map(AppMessageText::getId).collect(Collectors.toList()));
                for (AppMessageText appMessageText : appMessageTexts) {
                    if (appMessageTextsFromDb.stream().anyMatch(app -> app.getId().equals(appMessageText.getId())))
                        updatedRecords.add(appMessageText);
                }
                if (!CollectionUtils.isEmpty(updatedRecords))
                    appMessageTextRepository.saveAll(updatedRecords);
            }
        } catch (Exception ex) {
            log.error("Failed to update app messages. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to update app messages.");
        }
    }

    @Override
    @CacheEvict(value = "appMessages", allEntries = true)
    public void deleteAppMessageTexts(List<Integer> appMessageCodesRequest) {
        try {
            if (!CollectionUtils.isEmpty(appMessageCodesRequest)) {
                appMessageTextRepository.deleteAllById(appMessageCodesRequest);
            }
        } catch (Exception ex) {
            log.error("Failed to delete app messages. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to delete app messages.");
        }
    }
}
