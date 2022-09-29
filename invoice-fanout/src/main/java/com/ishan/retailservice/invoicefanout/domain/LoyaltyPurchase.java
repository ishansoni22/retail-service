package com.ishan.retailservice.invoicefanout.domain;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoyaltyPurchase {

  private String invoiceId;
  private String customerId;
  private String customerName;
  private BigDecimal purchaseValue;
  private int loyaltyPoints;
  private int totalLoyaltyPoints;

}
