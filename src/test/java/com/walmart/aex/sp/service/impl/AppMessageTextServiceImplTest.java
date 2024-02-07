package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextRequest;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.entity.AppMessageText;
import com.walmart.aex.sp.entity.AppMessageType;
import com.walmart.aex.sp.repository.AppMessageTextRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppMessageTextServiceImplTest {

    @InjectMocks
    private AppMessageTextServiceImpl appMessageTextService;

    @Mock
    private AppMessageTextRepository appMessageTextRepository;

    private List<AppMessageText> appMessageTextsDb;

    private List<AppMessageTextRequest> appMessageTextRequests;

    @Captor
    private ArgumentCaptor<List<AppMessageText>> messageArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Integer>> idArgumentCaptor;

    @BeforeEach
    void setup() {
        appMessageTextsDb = List.of(
                AppMessageText.builder().id(200).typeId(1).desc("test").longDesc("testing error")
                        .appMessageType(AppMessageType.builder().id(2).desc("ERROR").longDesc("Error").build())
                        .build(),
                AppMessageText.builder().id(201).typeId(2).desc("test").longDesc("testing warning")
                        .appMessageType(AppMessageType.builder().id(2).desc("WARNING").longDesc("Warning").build())
                        .build()
        );
    }

    @Test
    void testFindAllAppMessages() {
        when(appMessageTextRepository.findAll()).thenReturn(appMessageTextsDb);
        List<AppMessageTextResponse> appMessageTextResponses = appMessageTextService.getAllAppMessageText();
        assertNotNull(appMessageTextResponses);
        assertEquals(2, appMessageTextResponses.size());
        assertEquals("ERROR", appMessageTextResponses.stream().filter(appMessageTextResponse -> appMessageTextResponse.getId().equals(200)).map(AppMessageTextResponse::getTypeDesc).findFirst().orElse(null));
    }

    @Test
    void testAddAppMessages() {
        appMessageTextRequests = List.of(
                AppMessageTextRequest.builder().id(202).typeId(1).desc("Add").longDesc("testing add 1").build(),
                AppMessageTextRequest.builder().id(203).typeId(1).desc("Add").longDesc("testing add 2").build()
        );
        appMessageTextService.addAppMessageTexts(appMessageTextRequests);
        verify(appMessageTextRepository, times(1)).saveAll(messageArgumentCaptor.capture());
        assertNotNull(messageArgumentCaptor.getValue());
        assertEquals(2, messageArgumentCaptor.getValue().size());
    }

    @Test
    void testUpdateAppMessages() {
        appMessageTextRequests = List.of(
                AppMessageTextRequest.builder().id(201).typeId(1).desc("Update").longDesc("testing update 1").build(),
                AppMessageTextRequest.builder().id(202).typeId(1).desc("Update").longDesc("testing update 2").build()
        );
        when(appMessageTextRepository.findAllById(anyList())).thenReturn(appMessageTextsDb);
        appMessageTextService.updateAppMessageTexts(appMessageTextRequests);
        verify(appMessageTextRepository, times(1)).saveAll(messageArgumentCaptor.capture());
        assertNotNull(messageArgumentCaptor.getValue());
        assertEquals(1, messageArgumentCaptor.getValue().size());
    }

    @Test
    void testDeleteAppMessages() {
        List<Integer> request = List.of(200,201);
        appMessageTextService.deleteAppMessageTexts(request);
        verify(appMessageTextRepository, times(1)).deleteAllById(idArgumentCaptor.capture());
        assertNotNull(idArgumentCaptor.getValue());
        assertEquals(2, idArgumentCaptor.getValue().size());
    }

    @Test
    void getCodesByLevelTestForFinelineLevel() {
        Set<Integer> codes= new HashSet<>();
        codes.add(160);
        codes.add(163);
        codes.add(171);
        codes.add(172);
        Set<Integer> result = appMessageTextService.getCodesByLevel(codes,FINELINE);
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
        Set<Integer> result = appMessageTextService.getCodesByLevel(codes,STYLE);
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
        Set<Integer> result = appMessageTextService.getCodesByLevel(codes,CUSTOMER_CHOICE);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertTrue(result.contains(160));
        Assertions.assertTrue(result.contains(163));
    }

}