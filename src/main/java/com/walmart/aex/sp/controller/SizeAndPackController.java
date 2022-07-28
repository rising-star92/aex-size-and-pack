package com.walmart.aex.sp.controller;


import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.SizeAndPackResponse;
import com.walmart.aex.sp.service.SizeAndPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/size-and-pack/v1")
@Controller
@Api(consumes = MediaType.APPLICATION_JSON_VALUE)
public class SizeAndPackController {

    @Autowired
    SizeAndPackService sizeAndPackService;


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello");
    }

    @PostMapping(path = "/sizeAndPackService")
    public @ResponseBody
    ResponseEntity<SizeAndPackResponse> createPlanStrategy(@RequestBody PlanSizeAndPackDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sizeAndPackService.saveSizeAndPackData(request));
        } catch (Exception exp) {
            log.error("Exception occurred when creating a plan Strategy: {}", exp.getMessage());
            //throw new CustomException("Exception occurred when creating a plan Strategy: " + exp);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}




