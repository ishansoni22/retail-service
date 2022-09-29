package com.ishan.retailservice.invoicefanout.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductPurchase {

  private String product;
  private int quantity;

}
