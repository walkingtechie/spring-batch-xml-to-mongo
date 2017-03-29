package com.walking.techie.xmltomongo.model;


import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class Report {

  private int id;
  private Date date;
  private long impression;
  private int clicks;
  private BigDecimal earning;
}
