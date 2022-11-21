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

  @Value("${loyalty.store}")
  private String loyaltyStore;

  @Value("${best.seller.topic}")
  private String bestSellersTopic;

  @Value("${best.seller.store}")
  private String bestSellersStore;

  @Value("${sales.by.store.topic}")
  private String salesByStoreTopic;

  @Value("${sales.by.store.store}")
  private String salesByStoreStore;

  @Autowired
  private StreamsBuilder streamsBuilder;

  @Autowired
  private BestSellerService bestSellerService;

  @PostConstruct
  public void setUp() {
    TopologyBuilder.build(
        streamsBuilder,
        bestSellerService,
        invoiceTopic,
        shipmentTopic,
        loyaltyTopic,
        loyaltyStore,
        bestSellersTopic,
        bestSellersStore,
        salesByStoreTopic,
        salesByStoreStore
    );
  }

}
