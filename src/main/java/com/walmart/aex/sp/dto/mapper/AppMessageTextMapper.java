package com.walmart.aex.sp.dto.mapper;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextRequest;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.entity.AppMessageText;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AppMessageTextMapper {
    AppMessageTextMapper mapper = Mappers.getMapper(AppMessageTextMapper.class);

    List<AppMessageText> mapRequestToEntity(List<AppMessageTextRequest> requests);

    List<AppMessageTextResponse> mapEntityToResponse(List<AppMessageText> entity);

    default AppMessageTextResponse map(AppMessageText appMessageText) {
        return AppMessageTextResponse.builder()
                .id(appMessageText.getId())
                .typeDesc(appMessageText.getAppMessageType().getDesc())
                .desc(appMessageText.getDesc())
                .longDesc(appMessageText.getLongDesc())
                .build();
    }
}
