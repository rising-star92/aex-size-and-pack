package com.walmart.aex.sp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class  BuyQtyResponseInputs {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<BuyQntyResponseDTO> buyQtyFinelineInput(){
        String messageObj = "{\"codes\":[150,151]}";
        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(471l, 2, 50000, null, 34, null, 6419,
                null, 12228, null, 31507, null, 2855, null,
                1125, 1125, 1125, null,messageObj);

        BuyQntyResponseDTO buyQntyResponseDTO2 = new BuyQntyResponseDTO(471l, 2, 50000, null, 34, null, 6419,
                null, 12229, null, 31508, null, 2855, null,
                1125, 1125, 1125, null,messageObj);

        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO(471l, 2, 50000, null, 34, null, 6419,
                null, 12229, null, 31508, null, 2760, null,
                1125, 1125, 1125, null,messageObj);


        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        buyQntyResponseDTOS.add(buyQntyResponseDTO2);
        buyQntyResponseDTOS.add(buyQntyResponseDTO1);

        return buyQntyResponseDTOS;
    }

    public static List<BuyQntyResponseDTO> buyQtyStyleCcInput()
    {
        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12228, 31507, 2855, "34_2855_4_19_8", "34_2855_4_19_8_BLACK SOOT",
                1125, 1125, 1125,"{\"codes\":[150,151]}", 1125,1125,1125,2,"Red color family","Red", "34_2855_4_19_8_alt","Test_34_2855_4_19_8_BLACK SOOT","{\"codes\":[150,151]}");

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12228, 31507, 2855, "34_2855_4_19_8", "34_5471_3_24_001_CHINO TAN",
                1125, 1125, 1125, "{\"codes\":[150,151]}",1125,1125,1125,2, "Green color family","Green","Test_34_2855_4_19_8","Test_34_5471_3_24_001_CHINO TAN","{\"codes\":[150,151]}");

        BuyQntyResponseDTO buyQntyResponseDTO2 = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12229, 31508, 2855, "34_2855_4_20_8", "34_2855_4_20_8_BLACK SOOT",
                1125, 1125, 1125,"{\"codes\":[150,151]}", 1125,1125,1125,2,"Black color family", "Black", "Test_34_2855_4_20_8","Test_34_2855_4_20_8_BLACK SOOT","{\"codes\":[150,151]}");

        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO1);
        buyQntyResponseDTOS.add(buyQntyResponseDTO2);
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        return buyQntyResponseDTOS;
    }

    public static void convertChannelToStore(List<BuyQntyResponseDTO> buyQntyResponseDTOS)
    {
        for(BuyQntyResponseDTO buyQntyResponseDTO:buyQntyResponseDTOS ){
            buyQntyResponseDTO.setChannelId(1);
        }
    }

    public static String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }

    public static BuyQtyResponse buyQtyResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BuyQtyResponse.class);
    }

    public static BQFPResponse bQFPResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BQFPResponse.class);
    }

    public static BuyQtyRequest fetchBuyQtyRequestForOnline()
    {
        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(471l);
        buyQtyRequest.setChannel("Online");
        return buyQtyRequest;
    }

    public static BuyQtyRequest fetchBuyQtyRequestForStore()
    {
        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(471l);
        buyQtyRequest.setChannel("Store");
        return buyQtyRequest;
    }

}
