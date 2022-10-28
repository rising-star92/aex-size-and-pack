package com.walmart.aex.sp.controller;


import com.walmart.aex.sp.dto.commitmentreport.InitialSetPackRequest;
import com.walmart.aex.sp.dto.commitmentreport.InitialBumpSetResponse;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDeleteDTO;
import com.walmart.aex.sp.dto.planhierarchy.SizeAndPackResponse;
import com.walmart.aex.sp.service.SizeAndPackService;
import com.walmart.aex.sp.util.CommonUtil;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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

    private final CommonUtil commonUtil;
    @Autowired
    SizeAndPackService sizeAndPackService;

    public SizeAndPackController(CommonUtil commonUtil) {
        this.commonUtil = commonUtil;
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello");
    }

    @PostMapping(path = "/sizeAndPackService")
    public @ResponseBody
    ResponseEntity<SizeAndPackResponse> createLinePlan(@RequestBody PlanSizeAndPackDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sizeAndPackService.saveSizeAndPackData(commonUtil.cleanSPRequest(request)));
        } catch (Exception exp) {
            log.error("Exception occurred when creating a line plan: {}", exp.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PutMapping(path = "/sizeAndPackService")
    public @ResponseBody
    ResponseEntity<SizeAndPackResponse> updateLinePlan(@RequestBody PlanSizeAndPackDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sizeAndPackService.updateSizeAndPackData(commonUtil.cleanSPRequest(request)));
        } catch (Exception exp) {
            log.error("Exception occurred when updating a line plan : {}", exp.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @DeleteMapping(path = "/sizeAndPackService")
    public @ResponseBody
    ResponseEntity<SizeAndPackResponse> deleteLinePlan(@RequestBody PlanSizeAndPackDeleteDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sizeAndPackService.deleteSizeAndPackData(commonUtil.cleanSPDeleteRequest(request)));
        } catch (Exception exp) {
            log.error("Exception occurred when updating a line plan : {}", exp.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @QueryMapping
    public InitialBumpSetResponse getInitialAndBumpSetDetails(@Argument InitialSetPackRequest request) {
        return sizeAndPackService.getInitialAndBumpSetDetails(request);
    }

}




