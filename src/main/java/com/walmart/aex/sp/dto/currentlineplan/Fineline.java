package com.walmart.aex.sp.dto.currentlineplan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fineline  implements Serializable {
   private Integer finelineId;
   private String finelineName;
   private String altFinelineName;
   private String channel;
   private String eCommChannel;
   private Metrics metrics;
   private LikeAssociation likeAssociation;
}
