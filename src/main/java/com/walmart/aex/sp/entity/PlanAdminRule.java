package com.walmart.aex.sp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "plan_admin_rules", schema = "dbo")
public class PlanAdminRule {
    @Id
    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @MapsId(value = "deptNbr")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_nbr", nullable = false)
    @JsonIgnore
    private DeptAdminRule deptAdminRule;

    @Column(name = "dept_nbr", nullable = false)
    private Integer deptNbr;

    @Column(name = "repl_item_piece_rule", nullable = false)
    private Integer replItemPieceRule;

    @Column(name = "min_repl_item_units", nullable = false)
    private Integer minReplItemUnits;

    @Column(name = "create_ts")
    private Date createTs;

    @Column(name = "create_userid")
    private String createUserId;

    @Column(name = "last_modified_ts")
    private Date lastModifiedTs;

    @Column(name = "last_modified_userid")
    private String lastModifiedUserId;
}
