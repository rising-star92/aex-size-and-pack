package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReplenishmentServiceTest {

    @Mock
    private  FineLineReplenishmentRepository fineLineReplenishmentRepository;

    @Mock
    private  SpCustomerChoiceReplenishmentRepository spCustomerChoiceReplenishmentRepository;

    @Mock
    private  SizeListReplenishmentRepository sizeListReplenishmentRepository;

    @Mock
    private  CatgReplnPkConsRepository catgReplnPkConsRepository;

    @Mock
    private  SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

    @Mock
    private  FinelineReplnPkConsRepository finelineReplnPkConsRepository;

    @Mock
    private  StyleReplnPkConsRepository styleReplnConsRepository;

    @Mock
    private  CcReplnPkConsRepository ccReplnConsRepository;

    @Mock
    private  CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    @Mock
    private  ReplenishmentMapper replenishmentMapper;

    @Mock
    private  UpdateReplnConfigMapper updateReplnConfigMapper;

    @Mock
    private ReplenishmentService replenishmentService;

    @Test
    public void updateVnpkWhpkForCatgReplnConsTest(){
        UpdateVnPkWhPkReplnRequest request = new UpdateVnPkWhPkReplnRequest();
        request.setPlanId(1L);
        request.setChannel("Store");
        request.setLvl3Nbr(3);
        request.setVnpk(1);
        request.setWhpk(1);
        replenishmentService.updateVnpkWhpkForCatgReplnCons(request);
        Mockito.verify(replenishmentService, Mockito.times(1)).updateVnpkWhpkForCatgReplnCons(request);
    }

}