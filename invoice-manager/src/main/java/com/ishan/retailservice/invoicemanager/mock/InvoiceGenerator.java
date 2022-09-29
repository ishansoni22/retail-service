package com.ishan.retailservice.invoicemanager.mock;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.ishan.retailservice.invoicemanager.domain.Invoice;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InvoiceGenerator {

  @Value("${invoice.topic}")
  private String invoiceTopic;

  @Autowired
  private KafkaProducer<String, Invoice> invoiceProducer;

  @Scheduled(fixedRate = 5000)
  public void generateInvoices() {
    log.info("Generating mock invoices");

    Faker faker = Faker.instance();

    for (int i = 0; i < 10; i++) {
      Address storeAddress = faker.address();
      String storeCountry = storeAddress.country();

      if (i == 0) {
        storeCountry = "ONLINE";
      }
      CompletableFuture.runAsync(
          new Store(i, storeCountry, faker, invoiceProducer, invoiceTopic));
    }

  }

}
