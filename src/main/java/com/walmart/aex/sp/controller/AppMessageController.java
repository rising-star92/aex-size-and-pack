package com.walmart.aex.sp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextRequest;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.service.AppMessageTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.REQUEST_INVALID;
import static com.walmart.aex.sp.util.SizeAndPackConstants.SUCCESS_STATUS;

@RestController
@Slf4j
public class AppMessageController {

    private final AppMessageTextService appMessageTextService;

    private final CacheManager cacheManager;

    private final ObjectMapper objectMapper;

    public AppMessageController(AppMessageTextService appMessageTextService, CacheManager cacheManager, ObjectMapper objectMapper) {
        this.appMessageTextService = appMessageTextService;
        this.cacheManager = cacheManager;
        this.objectMapper = objectMapper;
    }

    @GetMapping("app-message/get-all")
    public ResponseEntity<List<AppMessageTextResponse>> getAppMessages() {
        log.info("Received request for getAppMessages");
        try {
            Cache cache = cacheManager.getCache("appMessages");
            if (null != cache)
                cache.clear();
            return ResponseEntity.status(HttpStatus.OK).body(appMessageTextService.getAllAppMessageText());
        } catch (Exception ex) {
            log.error("Exception while pulling appMessages");
            throw new CustomException("Exception: " + ex.getMessage());
        }
    }

    @MutationMapping
    public StatusResponse addAppMessages(@Argument List<AppMessageTextRequest> appMessageTextRequests) throws JsonProcessingException {
        log.info("Received request for addAppMessages: {}", objectMapper.writeValueAsString(appMessageTextRequests));
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            appMessageTextService.addAppMessageTexts(appMessageTextRequests);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception ex) {
            log.error("Exception occurred while adding AppMessage");
        }
        return statusResponse;
    }

    @MutationMapping
    public StatusResponse updateAppMessages(@Argument List<AppMessageTextRequest> appMessageTextRequests) throws JsonProcessingException {
        log.info("Received request for updateAppMessages: {}", objectMapper.writeValueAsString(appMessageTextRequests));
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            appMessageTextService.updateAppMessageTexts(appMessageTextRequests);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception ex) {
            log.error("Exception occurred while updating AppMessage");
        }
        return statusResponse;
    }

    @MutationMapping
    public StatusResponse deleteAppMessages(@Argument List<Integer> appMessageCodesRequest) throws JsonProcessingException {
        log.info("Received request for deleteAppMessages: {}", objectMapper.writeValueAsString(appMessageCodesRequest));
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            appMessageTextService.deleteAppMessageTexts(appMessageCodesRequest);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting AppMessage");
        }
        return statusResponse;
    }
}
