package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
public class ReplenishmentMapperTest {

        @InjectMocks
        ReplenishmentMapper replenishmentMapper;
        @Mock
        ReplenishmentResponseDTO replenishmentResponseDTO;
        private static final Integer finelineNbr=3470;
        private static final Long planId=471l;
        @Test
        public void testMapReplenishmentLvl2Sp() {

            ReplenishmentResponse replenishmentResponse= new ReplenishmentResponse();
            replenishmentResponseDTO=new ReplenishmentResponseDTO();
            replenishmentResponseDTO.setChannelId(2);
            replenishmentResponseDTO.setPlanId(planId);
            replenishmentMapper.mapReplenishmentLvl2Sp(replenishmentResponseDTO,replenishmentResponse,finelineNbr,"black");
            assertNotNull(replenishmentResponse);
            assertEquals(replenishmentResponse.getPlanId(),471l);
        }
}
