package com.ishan.retailservice.invoicefanout;

import com.ishan.retailservice.invoicefanout.domain.Invoice;
import com.ishan.retailservice.invoicefanout.domain.LoyaltyPurchase;
import com.ishan.retailservice.invoicefanout.domain.OrderLineItem;
import com.ishan.retailservice.invoicefanout.domain.ProductPurchase;
import com.ishan.retailservice.invoicefanout.domain.TopologyBuilder;
import com.ishan.retailservice.invoicefanout.port.adapters.config.AppSerdes;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TopologyTest {

  private static TopologyTestDriver topologyTestDriver;

  private static TestInputTopic<String, Invoice> invoiceTopic;

  private static TestOutputTopic<String, Invoice> shipmentTopic;

  private static TestOutputTopic<String, LoyaltyPurchase> loyaltyTopic;

  private static TestOutputTopic<String, ProductPurchase> productPurchaseTopic;

  @BeforeEach
  public void beforeAll() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();
    TopologyBuilder.build(
        streamsBuilder,
        "invoice-test",
        "shipment-test",
        "loyalty-test",
        "product-purchase-test"
        );

    Topology topology = streamsBuilder.build();

    topologyTestDriver = new TopologyTestDriver(topology);

    invoiceTopic = topologyTestDriver
        .createInputTopic("invoice-test", AppSerdes.String().serializer(),
            AppSerdes.invoice().serializer());

    shipmentTopic = topologyTestDriver
        .createOutputTopic("shipment-test", AppSerdes.String().deserializer(),
            AppSerdes.invoice().deserializer());

    loyaltyTopic = topologyTestDriver
        .createOutputTopic("loyalty-test", AppSerdes.String().deserializer(),
            AppSerdes.loyaltyPurchase().deserializer());

    productPurchaseTopic = topologyTestDriver
        .createOutputTopic("product-purchase-test", AppSerdes.String().deserializer(),
            AppSerdes.productPurchase().deserializer());

  }

  @Test
  public void testShipmentProcessor() {

    invoiceTopic.pipeInput(
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
            .build()
    );

    Assertions.assertEquals(0, shipmentTopic.getQueueSize());

  }

  @Test
  public void testTotalLoyaltyPoints() {
    invoiceTopic.pipeInput(
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
            .build()
    );

    invoiceTopic.pipeInput(
        Invoice.builder()
            .invoiceId(UUID.randomUUID().toString())
            .storeId(0)
            .storeCountry("US")
            .customerId("CUS-1")
            .customerType("PRIME")
            .total(BigDecimal.valueOf(200))
            .orderLineItems(
                Arrays.asList(
                    OrderLineItem.builder().product("PRODUCT-1").price(BigDecimal.valueOf(100)).quantity(1).build(),
                    OrderLineItem.builder().product("PRODUCT-2").price(BigDecimal.valueOf(50)).quantity(2).build()
                )
            )
            .build()
    );

    invoiceTopic.pipeInput(
        Invoice.builder()
            .invoiceId(UUID.randomUUID().toString())
            .storeId(0)
            .storeCountry("US")
            .customerId("CUS-1")
            .customerType("PRIME")
            .total(BigDecimal.valueOf(200))
            .orderLineItems(
                Arrays.asList(
                    OrderLineItem.builder().product("PRODUCT-2").price(BigDecimal.valueOf(50)).quantity(4).build()
                )
            )
            .build()
    );

    KeyValueStore<String, LoyaltyPurchase> loyaltyStore = topologyTestDriver
        .getKeyValueStore("loyalty-store-test");

    LoyaltyPurchase loyalty = loyaltyStore.get("CUS-1");
    Assertions.assertEquals(500, loyalty.getTotalLoyaltyPoints());

  }

}
