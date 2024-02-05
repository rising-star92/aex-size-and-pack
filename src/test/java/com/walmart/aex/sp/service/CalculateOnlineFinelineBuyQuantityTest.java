package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.exception.SizeAndPackException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateOnlineFinelineBuyQuantityTest extends CalculateFinelineBuyQuantityTest {

    @Test
    void initialSetCalculationTestOnLine() throws IOException {
        final String path = "/plan12fineline5160";
        CalculateBuyQtyResponse calculateBuyQtyResponse = calculateBuyQtyResponseFromJson(path.concat("/calculateBuyQtyResponse"));
        CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest = calculateBuyQtyParallelRequestFromJson(path.concat("/calculateBuyQtyParallelRequest"));
        FinelineDto finelineDto = finelineDtoFromJson(path.concat("/fineLineDto"));
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        calculateBuyQtyResponse = calculateOnlineFinelineBuyQuantity.calculateOnlineBuyQty(calculateBuyQtyParallelRequest, finelineDto, bqfpResponse, calculateBuyQtyResponse);
        assertEquals(5568, calculateBuyQtyResponse.getMerchCatgReplPacks().get(0).getFinalBuyUnits().intValue());
        assertEquals(5568, calculateBuyQtyResponse.getMerchCatgReplPacks().get(0).getReplUnits().intValue());
        assertEquals(464, calculateBuyQtyResponse.getMerchCatgReplPacks().get(0).getReplPackCnt().intValue());
    }

    CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequestFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), CalculateBuyQtyParallelRequest.class);
    }

    FinelineDto finelineDtoFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), FinelineDto.class);
    }

    CalculateBuyQtyResponse calculateBuyQtyResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), CalculateBuyQtyResponse.class);
    }

}
