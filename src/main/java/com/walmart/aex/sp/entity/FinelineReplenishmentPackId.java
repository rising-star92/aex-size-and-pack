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
public class FinelineReplenishmentPackId implements Serializable
{
    @Embedded
    private SubReplenishmentPackId subReplenishmentPackId;

    @Column(name="fineline_nbr",nullable = false)
    private Integer finelineNbr;
}
