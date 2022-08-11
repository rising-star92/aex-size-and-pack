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
public class StyleReplPackId implements Serializable
{
    @Embedded
    private FinelineReplPackId finelineReplPackId;

    @Column(name="style_nbr",nullable = false)
    private String styleNbr;
}