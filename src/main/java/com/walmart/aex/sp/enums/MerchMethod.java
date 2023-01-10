package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum MerchMethod {

   HANGING(1, "HANGING"),
   FOLDED(2, "FOLDED");

   private Integer id;
   private String description;

   MerchMethod(Integer id, String description) {
      this.id = id;
      this.description = description;
   }

   public Integer getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public static Integer getMerchMethodIdFromDescription(String description) {
      return Stream.of(values())
            .filter(mm -> mm.getDescription().toUpperCase().equals(description))
            .findFirst().map(MerchMethod::getId).orElse(null);
   }
}
