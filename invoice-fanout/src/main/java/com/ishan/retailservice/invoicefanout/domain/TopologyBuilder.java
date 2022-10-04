package com.ishan.retailservice.invoicefanout.domain;

import com.ishan.retailservice.invoicefanout.port.adapters.config.AppSerdes;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

public class TopologyBuilder {

  private TopologyBuilder() {
  }

  public static void build(
      StreamsBuilder builder,
      String invoiceTopic,
      String shipmentTopic,
      String loyaltyTopic, String loyaltyStore,
      String productPurchaseTopic, String bestSellersTopic, String bestSellersStore) {

    KStream<String, Invoice> invoiceStream = builder
        .stream(invoiceTopic,
            Consumed.with(Serdes.String(), AppSerdes.Invoice()));

    /*
    Filter online orders
    (These orders need to be shipped to the customer's selected delivery address)
     Push them to the shipment topic
     */
    invoiceStream
        .filter((key, invoice) -> invoice.getStoreId() == 0)
        .to(shipmentTopic, Produced.with(Serdes.String(), AppSerdes.Invoice()));

    /*
    Filter orders by prime customers
    Create a loyalty purchase event from the invoice and send it to the
    loyalty topic. This should also contain the total loyalty points
     */
    invoiceStream
        .filter((key, invoice) -> "PRIME".equals(invoice.getCustomerType()))
        .mapValues(TopologyBuilder::toLoyalty)
        .groupBy((key, loyalty) -> loyalty.getCustomerId(),
            Grouped.with(Serdes.String(), AppSerdes.Loyalty()))
        .aggregate(
            LoyaltyPurchase::new,
            (customerId, currentLoyalty, aggregatedLoyalty) ->
                new LoyaltyPurchase.LoyaltyPurchaseBuilder()
                    .customerId(customerId)
                    .customerName(currentLoyalty.getCustomerName())
                    .invoiceId(currentLoyalty.getInvoiceId())
                    .purchaseValue(currentLoyalty.getPurchaseValue())
                    .loyaltyPoints(currentLoyalty.getLoyaltyPoints())
                    .totalLoyaltyPoints(aggregatedLoyalty.getTotalLoyaltyPoints() + currentLoyalty
                        .getLoyaltyPoints())
                    .build(),
            Materialized.<String, LoyaltyPurchase, KeyValueStore<Bytes, byte[]>>as(loyaltyStore)
                .withKeySerde(Serdes.String()).withValueSerde(AppSerdes.Loyalty())
        ).toStream().to(loyaltyTopic, Produced.with(Serdes.String(), AppSerdes.Loyalty()));

    /*
     Push product purchases to the product purchase topic
     */
    KStream<String, ProductPurchase> productPurchaseStream
        = invoiceStream.flatMapValues(TopologyBuilder::toProductPurchase);

    productPurchaseStream
        .to(productPurchaseTopic, Produced.with(Serdes.String(), AppSerdes.Product()));

    /*
    Best selling products
     */

    productPurchaseStream
        .map((invoiceId, productPurchase)
            -> new KeyValue<>(productPurchase.getProduct(), productPurchase.getQuantity()))
        .groupByKey(Grouped.with(Serdes.String(), Serdes.Integer()))
        .aggregate(
            () -> 0,
            (product, latestPurchaseQty, totalPurchasedQty) -> latestPurchaseQty + totalPurchasedQty,
            Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as(bestSellersStore)
                .withKeySerde(Serdes.String()).withValueSerde(Serdes.Integer())
        ).toStream().to(bestSellersTopic, Produced.with(Serdes.String(), Serdes.Integer()));

  }

  private static LoyaltyPurchase toLoyalty(Invoice invoice) {
    String customerId = invoice.getCustomerId();
    LoyaltyPurchase loyaltyPurchase = new LoyaltyPurchase();
    loyaltyPurchase.setInvoiceId(invoice.getInvoiceId());
    loyaltyPurchase.setCustomerId(customerId);
    loyaltyPurchase.setCustomerName(invoice.getCustomerName());
    loyaltyPurchase.setPurchaseValue(invoice.getTotal());
    int earnedPoints =
        invoice.getTotal().round(new MathContext(0, RoundingMode.FLOOR)).intValue();
    loyaltyPurchase.setLoyaltyPoints(earnedPoints);
    return loyaltyPurchase;
  }

  private static List<ProductPurchase> toProductPurchase(Invoice invoice) {
    return invoice.getOrderLineItems()
        .stream()
        .map(orderLineItem ->
            ProductPurchase.builder()
                .product(orderLineItem.getProduct())
                .quantity(orderLineItem.getQuantity())
                .build()
        ).collect(Collectors.toList());
  }

}
