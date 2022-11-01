package com.walmart.aex.sp.repository.common;

import com.walmart.aex.sp.repository.*;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class PackOptimizationCommonRepository {
    private final MerchPackOptimizationRepository merchPackOptimizationRepository;
    private final SubCatgPackOptimizationRepository subCatgPackOptimizationRepository;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private final StylePackOptimizationRepository stylePackOptimizationRepository;
    private final FinelinePackOptConsRepository finelinePackOptConsRepository;

}
