package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextRequest;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;

import java.util.List;

public interface AppMessageTextService {
    List<AppMessageTextResponse> getAllAppMessageText();
    void addAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests);
    void updateAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests);
    void deleteAppMessageTexts(List<Integer> appMessageCodeRequest);
}
