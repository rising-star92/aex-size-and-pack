package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "analytics_ml_send", schema = "dbo")
public class AnalyticsMlSend {

    @Id
    @Column(name = "analytics_send_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private BigInteger analyticsSendId;

    @Column(name="plan_id")
    private Long planId;

    @Column(name="strategy_id")
    private BigInteger strategyId;

    @Column(name="analytics_cluster_id")
    private BigInteger analyticsClusterId;

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

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @JoinColumn(name = "run_status_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = RunStatusText.class, fetch = FetchType.LAZY)
    private RunStatusText runStatusText;

    @Column(name = "run_status_code", nullable = false)
    private Integer runStatusCode;

    @Column(name="analytics_send_desc")
    private String analyticsSendDesc;

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

    @OneToMany(mappedBy = "analyticsMlSend", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnalyticsMlChildSend> analyticsMlChildSend;

}
