package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class FinelinePlanId implements Serializable {
    @Embedded
    private SubCatPlanId subCatPlanId;

    @Column(name = "fineline_nbr", nullable = false)
    private Integer finelineNbr;
}
