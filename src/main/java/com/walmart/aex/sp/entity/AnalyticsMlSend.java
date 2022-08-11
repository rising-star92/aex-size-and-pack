package com.walmart.aex.sp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "analytics_ml_send", schema = "dbo")
public class AnalyticsMlSend {

    @Id
    @Column(name = "analytics_send_id", nullable = false)
    private BigInteger analyticsSendId;

    @Column(name="plan_id")
    private Long planId;

    @Column(name="strategy_id")
    private Integer strategyId;

    @Column(name="analytics_cluster_id")
    private Integer analyticsClusterId;

    @Column(name="rpt_lvl_0_nbr")
    private Integer lvl0Nbr;

    @Column(name="rpt_lvl_1_nbr")
    private Integer lvl1Nbr;

    @Column(name="rpt_lvl_2_nbr")
    private Integer lvl2Nbr;

    @Column(name="rpt_lvl_3_nbr")
    private Integer lvl3Nbr;

    @Column(name="rpt_lvl_4_nbr")
    private Integer lvl4Nbr;

    @Column(name="fineline_nbr")
    private Integer finelineNbr;

    @Column(name="style_nbr")
    private String styleNbr;

    @Column(name="customer_choice")
    private String customerChoice;

    @JoinColumn(name = "run_status_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = RunStatusText.class, fetch = FetchType.LAZY)
    private RunStatusText runStatusCode;

    @Column(name="analytics_send_desc")
    private String analyticsSendDesc;

    @Column(name="process_id")
    private String processId;

    @Column(name="start_ts")
    private Date startTs;

    @Column(name="end_ts")
    private Date endTs;

    @Column(name="retry_cnt")
    private Integer retryCnt;

    @Column(name="payload_obj")
    private String payloadObj;

    @Column(name="return_message")
    private String returnMessage;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;



}
