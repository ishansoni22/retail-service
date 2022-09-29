package com.ishan.retailservice.invoicemanager.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Address {

  private String building;
  private String street;
  private String city;
  private String state;
  private String zipCode;
  private String country;

}
