package com.ishan.retailservice.invoicefanout.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyPurchase {

  private String invoiceId;
  private String customerId;
  private String customerName;
  private BigDecimal purchaseValue;
  private int loyaltyPoints;
  private int totalLoyaltyPoints;

}
