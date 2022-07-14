package com.walmart.aex.sp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fp_strategy_text", schema = "dbo")
public class FpStrategyText {
    @Id
    @Column(name = "flow_strategy_code", nullable = false)
    private Integer flowStrategyCode;

    @Column(name = "flow_strategy_desc", nullable = false)
    private String  flowStrategyDesc;



}
