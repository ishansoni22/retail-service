package com.ishan.retailservice.invoicefanout;

import com.ishan.retailservice.invoicefanout.domain.Invoice;
import com.ishan.retailservice.invoicefanout.domain.OrderLineItem;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

public class InvoiceFixtures {

  public static Invoice order1() {
    return
        Invoice.builder()
            .invoiceId(UUID.randomUUID().toString())
            .storeId(1)
            .storeCountry("US")
            .customerId("CUS-1")
            .customerType("PRIME")
            .total(BigDecimal.valueOf(100))
            .orderLineItems(
                Arrays.asList(
                    OrderLineItem.builder().product("PRODUCT-1").price(BigDecimal.valueOf(100)).quantity(1).build()
                )
            )
            .build();
  }

  public static Invoice order2() {
    return
        Invoice.builder()
            .invoiceId(UUID.randomUUID().toString())
            .storeId(0)
            .storeCountry("ONLINE")
            .customerId("CUS-1")
            .customerType("PRIME")
            .total(BigDecimal.valueOf(200))
            .orderLineItems(
                Arrays.asList(
                    OrderLineItem.builder().product("PRODUCT-1").price(BigDecimal.valueOf(100)).quantity(1).build(),
                    OrderLineItem.builder().product("PRODUCT-2").price(BigDecimal.valueOf(50)).quantity(2).build()
                )
            )
            .build();
  }

  public static Invoice order3() {
    return
        Invoice.builder()
            .invoiceId(UUID.randomUUID().toString())
            .storeId(2)
            .storeCountry("IN")
            .customerId("CUS-1")
            .customerType("PRIME")
            .total(BigDecimal.valueOf(425))
            .orderLineItems(
                Arrays.asList(
                    OrderLineItem.builder().product("PRODUCT-1").price(BigDecimal.valueOf(100)).quantity(1).build(),
                    OrderLineItem.builder().product("PRODUCT-2").price(BigDecimal.valueOf(50)).quantity(2).build(),
                    OrderLineItem.builder().product("PRODUCT-3").price(BigDecimal.valueOf(75)).quantity(3).build()
                )
            )
            .build();
  }

  public static Invoice order4() {
    return
        Invoice.builder()
            .invoiceId(UUID.randomUUID().toString())
            .storeId(1)
            .storeCountry("US")
            .customerId("CUS-2")
            .customerType("PRIME")
            .total(BigDecimal.valueOf(250))
            .orderLineItems(
                Arrays.asList(
                    OrderLineItem.builder().product("PRODUCT-4").price(BigDecimal.valueOf(25)).quantity(10).build()
                )
            )
            .build();
  }

}
