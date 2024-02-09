package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextRequest;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;

import java.util.List;
import java.util.Set;

public interface AppMessageTextService {
    List<AppMessageTextResponse> getAllAppMessageText();
    void addAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests);
    void updateAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests);
    void deleteAppMessageTexts(List<Integer> appMessageCodeRequest);
    List<AppMessageTextResponse> getAppMessagesByIds(Set<Integer> validationCodes);
    Set<Integer> getHierarchyIds(Set<Integer> codes);
}
