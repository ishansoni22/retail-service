package com.ishan.retailservice.invoicefanout;

import com.ishan.retailservice.invoicefanout.domain.BestSellerService;
import com.ishan.retailservice.invoicefanout.domain.Invoice;
import com.ishan.retailservice.invoicefanout.domain.LoyaltyPurchase;
import com.ishan.retailservice.invoicefanout.domain.TopologyBuilder;
import com.ishan.retailservice.invoicefanout.port.adapters.config.AppSerdes;
import java.math.BigDecimal;
import java.util.List;
import org.apache.kafka.common.serialization.Serdes;
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

  private static TestOutputTopic<String, Integer> totalProductPurchaseTopic;

  private static TestOutputTopic<String, Double> salesByStoreTopic;

  private static final String INVOICE_TOPIC = "invoices-test";
  private static final String SHIPMENT_TOPIC = "shipments-test";
  private static final String LOYALTY_TOPIC = "loyalty-test";
  private static final String LOYALTY_STORE = "loyalty-points-test";
  private static final String BEST_SELLERS_TOPIC = "best-sellers-test";
  private static final String BEST_SELLERS_STORE = "best-sellers-store-test";
  private static final String SALES_BY_STORE_TOPIC = "store-sales-test";
  private static final String SALES_BY_STORE_STORE = "store-sales-store-test";

  @BeforeEach
  public void beforeAll() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();
    TopologyBuilder.build(
        streamsBuilder,
        new BestSellerService() {
          @Override
          public void registerProductPurchase(String product, int quantity) {
          }
          @Override
          public List<String> getTopNBestSellingProducts(int n) {
            return null;
          }
        },
        INVOICE_TOPIC,
        SHIPMENT_TOPIC,
        LOYALTY_TOPIC,
        LOYALTY_STORE,
        BEST_SELLERS_TOPIC,
        BEST_SELLERS_STORE,
        SALES_BY_STORE_TOPIC,
        SALES_BY_STORE_STORE
    );

    Topology topology = streamsBuilder.build();

    topologyTestDriver = new TopologyTestDriver(topology);

    invoiceTopic = topologyTestDriver
        .createInputTopic(INVOICE_TOPIC, Serdes.String().serializer(),
            AppSerdes.Invoice().serializer());

    shipmentTopic = topologyTestDriver
        .createOutputTopic(SHIPMENT_TOPIC, Serdes.String().deserializer(),
            AppSerdes.Invoice().deserializer());

    loyaltyTopic = topologyTestDriver
        .createOutputTopic(LOYALTY_TOPIC, Serdes.String().deserializer(),
            AppSerdes.Loyalty().deserializer());

    totalProductPurchaseTopic = topologyTestDriver
        .createOutputTopic(BEST_SELLERS_TOPIC, Serdes.String().deserializer(),
            Serdes.Integer().deserializer());

    salesByStoreTopic = topologyTestDriver
        .createOutputTopic(SALES_BY_STORE_TOPIC, Serdes.String().deserializer(),
            Serdes.Double().deserializer());

  }

  @Test
  public void testShipmentProcessor() {
    invoiceTopic.pipeInput(InvoiceFixtures.order1());
    invoiceTopic.pipeInput(InvoiceFixtures.order2());
    invoiceTopic.pipeInput(InvoiceFixtures.order3());
    invoiceTopic.pipeInput(InvoiceFixtures.order4());
    Assertions.assertEquals(1, shipmentTopic.getQueueSize());
  }

  @Test
  public void testTotalLoyaltyPointsProcessor() {
    invoiceTopic.pipeInput(InvoiceFixtures.order1());
    invoiceTopic.pipeInput(InvoiceFixtures.order2());
    invoiceTopic.pipeInput(InvoiceFixtures.order3());
    invoiceTopic.pipeInput(InvoiceFixtures.order4());

    KeyValueStore<String, LoyaltyPurchase> loyaltyStore = topologyTestDriver
        .getKeyValueStore(LOYALTY_STORE);

    LoyaltyPurchase loyalty1 = loyaltyStore.get("CUS-1");
    Assertions.assertEquals(725, loyalty1.getTotalLoyaltyPoints());
    Assertions.assertEquals(BigDecimal.valueOf(425), loyalty1.getPurchaseValue());

    LoyaltyPurchase loyalty2 = loyaltyStore.get("CUS-2");
    Assertions.assertEquals(250, loyalty2.getTotalLoyaltyPoints());
  }

  @Test
  public void testBestSellingProductsProcessor() {
    invoiceTopic.pipeInput(InvoiceFixtures.order1());
    invoiceTopic.pipeInput(InvoiceFixtures.order2());
    invoiceTopic.pipeInput(InvoiceFixtures.order3());
    invoiceTopic.pipeInput(InvoiceFixtures.order4());

    KeyValueStore<String, Integer> bestSellingProductStore = topologyTestDriver
        .getKeyValueStore(BEST_SELLERS_STORE);

    Integer product1Qty = bestSellingProductStore.get("PRODUCT-1");
    Integer product2Qty = bestSellingProductStore.get("PRODUCT-2");
    Integer product3Qty = bestSellingProductStore.get("PRODUCT-3");
    Integer product4Qty = bestSellingProductStore.get("PRODUCT-4");


    Assertions.assertEquals(3, product1Qty);
    Assertions.assertEquals(4, product2Qty);
    Assertions.assertEquals(3, product3Qty);
    Assertions.assertEquals(10, product4Qty);
  }

  @Test
  public void testTotalSalesByStoreProcessor() {
    invoiceTopic.pipeInput(InvoiceFixtures.order1());
    invoiceTopic.pipeInput(InvoiceFixtures.order2());
    invoiceTopic.pipeInput(InvoiceFixtures.order3());
    invoiceTopic.pipeInput(InvoiceFixtures.order4());

    KeyValueStore<String, Double> salesStore = topologyTestDriver
        .getKeyValueStore(SALES_BY_STORE_STORE);

    Double onlineSales = salesStore.get(TopologyBuilder.salesStoreKeyByMonth(0));
    Double store1Sales = salesStore.get(TopologyBuilder.salesStoreKeyByMonth(1));
    Double store2Sales = salesStore.get(TopologyBuilder.salesStoreKeyByMonth(2));

    Assertions.assertEquals(200D, onlineSales);
    Assertions.assertEquals(350D, store1Sales);
    Assertions.assertEquals(425D, store2Sales);
  }

}
