package com.walmart.aex.sp.entity;



import lombok.*;

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
    private BigInteger analyticssendid;

    @JoinColumn(name = "run_status_text", insertable = false, updatable = false)
    @ManyToOne(targetEntity = RunStatusText.class, fetch = FetchType.LAZY)
    private RunStatusText runstatuscode;


    @Column(name="analytics_send_desc")
    private String analyticssend;

    @Column(name="process_id")
    private String processid;

    @Column(name="start_ts")
    private Date startts;

    @Column(name="end_ts")
    private Date endts;

    @Column(name="retry_cnt")
    private Integer retrycnt;

    @Column(name="payload_obj")
    private String payloadobj;

    @Column(name="return_message")
    private String returnmessag;



}
