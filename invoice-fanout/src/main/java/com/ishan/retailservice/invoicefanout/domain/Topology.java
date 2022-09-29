package com.ishan.retailservice.invoicefanout.domain;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Topology {

  @Value("${invoice.topic}")
  private String invoiceTopic;

  @Value("${shipment.topic}")
  private String shipmentTopic;

  @Value("${loyalty.topic}")
  private String loyaltyTopic;

  @Value("${product.purchase.topic}")
  private String productPurchaseTopic;

  @Autowired
  private StreamsBuilder streamsBuilder;

  @PostConstruct
  public void setUp() {
    TopologyBuilder.build(
        streamsBuilder,
        invoiceTopic,
        shipmentTopic,
        loyaltyTopic,
        productPurchaseTopic
    );
  }

}
