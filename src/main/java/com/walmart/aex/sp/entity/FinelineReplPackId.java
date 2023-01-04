package com.walmart.aex.sp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class FinelineReplPackId implements Serializable
{
    @Embedded
    private SubCatgReplPackId subCatgReplPackId;

    @Column(name="fineline_nbr",nullable = false)
    private Integer finelineNbr;

    public FinelineReplPackId(SubCatgReplPackId subCatgReplPackId) {
        this.subCatgReplPackId = subCatgReplPackId;
    }
}
