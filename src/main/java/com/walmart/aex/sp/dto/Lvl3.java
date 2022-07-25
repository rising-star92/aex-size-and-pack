package com.walmart.aex.sp.dto;

import java.util.List;

import lombok.Data;

@Data
public class Lvl3 {

    private Integer lvl3Nbr;

    private String lvl3Name;
    private Constraints constraints;
    
	 private List<Lvl4> lvl4List; 
    

}
