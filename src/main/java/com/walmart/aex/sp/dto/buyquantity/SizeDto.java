package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;
import java.util.Objects;

import com.walmart.aex.sp.dto.bqfp.Replenishment;

@Data
public class SizeDto {
    private Integer ahsSizeId;
    private Integer sizeId;
    private String sizeDesc;
    private Metadata metadata;
    private MetricsDto metrics;
    private List<Replenishment> replenishments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeDto sizeDto = (SizeDto) o;
        return Objects.equals(ahsSizeId, sizeDto.ahsSizeId) &&
                Objects.equals(sizeDesc, sizeDto.sizeDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ahsSizeId, sizeDesc);
    }
}
