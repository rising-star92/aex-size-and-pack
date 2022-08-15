package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.Objects;

@Data
public class SizeDto {
    private Integer ahsSizeId;
    private Integer sizeId;
    private String sizeDesc;
    private MetricsDto metrics;

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
