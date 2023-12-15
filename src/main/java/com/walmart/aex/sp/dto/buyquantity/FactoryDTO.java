package com.walmart.aex.sp.dto.buyquantity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class FactoryDTO {
    private Integer finelineNbr;
    private String styleNbr;
    private String ccId;
    private String factoryId;
    private String factoryName;

    @Override
    public int hashCode() {
        return Objects.hash(factoryId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactoryDTO that = (FactoryDTO) o;
        return Objects.equals(factoryId, that.factoryId);
    }
}
