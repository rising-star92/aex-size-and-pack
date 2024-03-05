package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.*;

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
    private DeptAdminRule deptAdminRule;

    @Column(name = "dept_nbr", nullable = false)
    private Integer deptNbr;

    @Column(name = "repl_item_piece_rule", nullable = false)
    private Integer replItemPieceRule;

    @Column(name = "min_repl_item_units", nullable = false)
    private Integer minReplItemUnits;
}
