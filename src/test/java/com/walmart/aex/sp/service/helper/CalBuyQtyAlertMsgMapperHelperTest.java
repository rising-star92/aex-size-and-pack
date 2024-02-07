package com.walmart.aex.sp.service.helper;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@ExtendWith(MockitoExtension.class)
class CalBuyQtyAlertMsgMapperHelperTest {

    @InjectMocks
    CalBuyQtyAlertMsgMapperHelper calBuyQtyAlertMsgMapperHelper;

    @Test
    void getCodesByLevelTestForFinelineLevel() {
        Set<Integer> codes= new HashSet<>();
        codes.add(160);
        codes.add(163);
        codes.add(171);
        codes.add(172);
        Set<Integer> result = calBuyQtyAlertMsgMapperHelper.getCodesByLevel(codes,FINELINE);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(301));
        Assertions.assertTrue(result.contains(303));
    }

    @Test
    void getCodesByLevelTestForStyleLevel() {
        Set<Integer> codes= new HashSet<>();
        codes.add(160);
        codes.add(163);
        codes.add(171);
        codes.add(172);
        Set<Integer> result = calBuyQtyAlertMsgMapperHelper.getCodesByLevel(codes,STYLE);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(302));
        Assertions.assertTrue(result.contains(304));
    }

    @Test
    void getCodesByLevelTestForCCLevel() {
        Set<Integer> codes= new HashSet<>();
        codes.add(160);
        codes.add(163);
        codes.add(171);
        codes.add(172);
        Set<Integer> result = calBuyQtyAlertMsgMapperHelper.getCodesByLevel(codes,CUSTOMER_CHOICE);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertTrue(result.contains(160));
        Assertions.assertTrue(result.contains(163));
    }

}