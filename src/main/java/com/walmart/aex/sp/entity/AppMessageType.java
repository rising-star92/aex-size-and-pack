package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_message_type_text", schema = "dbo")
public class AppMessageType {

    @Id
    @Column(name = "app_message_type_code", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app_message_type_desc")
    private String desc;

    @Column(name = "app_message_type_long_desc")
    private String longDesc;

    @OneToMany(mappedBy = "appMessageType", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppMessageText> appMessageTexts;
}
