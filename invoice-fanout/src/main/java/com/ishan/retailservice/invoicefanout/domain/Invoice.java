package com.ishan.retailservice.invoicefanout.domain;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

  private String invoiceId;
  private String time;
  /* store with id 0 is the online store */
  private int storeId;
  private String storeCountry;
  private String customerId;
  private String customerName;
  private String customerType;
  private Address address;
  private List<OrderLineItem> orderLineItems;
  private BigDecimal total;
  private PaymentInfo paymentInfo;

}
