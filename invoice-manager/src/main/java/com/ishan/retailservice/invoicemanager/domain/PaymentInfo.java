package com.ishan.retailservice.invoicemanager.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentInfo {

  private String paymentType;
  private String upiId;
  private String creditCardType;
  private String creditCardNumber;
  private String creditCardExpiry;

}
