package com.walmart.aex.sp.dto.mapper;


import com.walmart.aex.sp.dto.packoptimization.PackOptConstraintResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FineLineMapper{

    FineLineMapper fineLineMapper = Mappers.getMapper(FineLineMapper.class);

    @Mapping(source="finelineNbr",target="fineLineNbr")
    FineLineMapperDto packOptConstraintResponseDTOToFineLineMapperDto(PackOptConstraintResponseDTO packOptConstraintResponseDTO);
}
