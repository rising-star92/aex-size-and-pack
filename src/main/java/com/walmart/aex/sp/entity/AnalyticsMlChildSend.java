package com.walmart.aex.sp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "analytics_child_send", schema = "dbo")
public class AnalyticsMlChildSend {
    @Id
    @Column(name = "analytics_send_id", nullable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger analyticsChildSendId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "analytics_send_id", referencedColumnName = "analytics_send_id", nullable = false, insertable = false, updatable = false)
    private AnalyticsMlSend analyticsMlSend;

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

    @Column(name="analytics_job_id")
    private String analyticsJobId;

    @Column(name="bump_pack_nbr")
    private Integer bumpPackNbr;
}
