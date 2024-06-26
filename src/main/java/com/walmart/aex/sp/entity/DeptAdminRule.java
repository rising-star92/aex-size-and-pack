package com.walmart.aex.sp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dept_admin_rules", schema = "dbo")
public class DeptAdminRule {

    @Id
    @Column(name = "dept_nbr", nullable = false)
    private Integer deptNbr;

    @Column(name = "repl_item_piece_rule", nullable = false)
    private Integer replItemPieceRule;

    @Column(name = "min_repl_item_units", nullable = false)
    private Integer minReplItemUnits;

    @OneToMany(mappedBy = "deptAdminRule", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlanAdminRule> planAdminRules;
}
