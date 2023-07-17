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
@Table(name = "run_status_text", schema = "dbo")
public class RunStatusText {

    @Id
    @Column(name = "run_status_code", nullable = false)
     private Integer runStatusCode;

    @Column(name = "run_status_desc", nullable = false)
     private String runStatusDesc;

    @Column(name = "run_status_long_desc", nullable = false)
    private String runStatusLongDesc;

}
