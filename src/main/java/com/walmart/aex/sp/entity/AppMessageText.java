package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_message_text", schema = "dbo")
public class AppMessageText {

    @Id
    @Column(name = "app_message_code", nullable = false)
    private Integer id;

    @MapsId(value = "typeId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "app_message_type_code", nullable = false)
    private AppMessageType appMessageType;

    @Column(name = "app_message_type_code")
    private Integer typeId;

    @Column(name = "app_message_desc")
    private String desc;

    @Column(name = "app_message_long_desc")
    private String longDesc;
}
