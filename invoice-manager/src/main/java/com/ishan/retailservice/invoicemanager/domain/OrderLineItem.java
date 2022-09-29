package com.ishan.retailservice.invoicemanager.domain;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderLineItem {

  private String product;
  private BigDecimal price;
  private int quantity;

}
