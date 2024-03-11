package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.service.StoreClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/store-cluster")
@RestController
public class StoreClusterController {

    private final StoreClusterService storeClusterService;

    public StoreClusterController(StoreClusterService storeClusterService) {
        this.storeClusterService = storeClusterService;
    }

    @DeleteMapping(path = "/cache")
    public void invalidateStoreClusterCache() {
        storeClusterService.invalidateStoreClusterCache();
    }

}
