package com.ishan.retailservice.invoicemanager.mock;

import com.github.javafaker.Business;
import com.github.javafaker.Faker;
import com.ishan.retailservice.invoicemanager.domain.Address;
import com.ishan.retailservice.invoicemanager.domain.Invoice;
import com.ishan.retailservice.invoicemanager.domain.OrderLineItem;
import com.ishan.retailservice.invoicemanager.domain.PaymentInfo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
public class Store implements Runnable {

  private int storeId;
  private String storeCountry;
  private Faker faker;
  private KafkaProducer<String, Invoice> producer;
  private String invoiceTopic;

  public Store(int storeId, String storeCountry,
      Faker faker, KafkaProducer<String, Invoice> producer, String invoiceTopic) {
    this.storeId = storeId;
    this.storeCountry = storeCountry;
    this.faker = faker;
    this.producer = producer;
    this.invoiceTopic = invoiceTopic;
  }

  @Override
  public void run() {
    for (int i = 0; i < 2; i++) {
      Invoice invoice = new Invoice();
      invoice.setInvoiceId(UUID.randomUUID().toString());
      invoice.setTime(LocalDateTime.now().toString());
      invoice.setStoreId(this.storeId);
      invoice.setStoreCountry(this.storeCountry);
      invoice.setCustomerId("customer-" + faker.random().nextInt(1, 10000));
      String name = this.faker.name().fullName();
      invoice.setCustomerName(name);
      if (faker.random().nextBoolean()) {
        invoice.setCustomerType("PRIME");
      } else {
        invoice.setCustomerType("NORMAL");
      }

      /*
      Only online store orders requires a delivery address
       */
      if (this.storeId == 0) {
        Address address = new Address();
        com.github.javafaker.Address deliveryAddress = this.faker.address();
        address.setBuilding(deliveryAddress.buildingNumber());
        address.setStreet(deliveryAddress.streetAddress());
        address.setCity(deliveryAddress.city());
        address.setState(deliveryAddress.state());
        address.setCountry(deliveryAddress.country());
        address.setZipCode(deliveryAddress.zipCode());

        invoice.setAddress(address);
      }

      int products = this.faker.random().nextInt(1, 5);
      List<OrderLineItem> orderLineItems = new ArrayList<>();
      BigDecimal total = BigDecimal.ZERO;

      for (int p = 1; p <= products; p++) {
        OrderLineItem lineItem = new OrderLineItem();
        lineItem.setProduct(this.faker.commerce().productName());
        BigDecimal price = new BigDecimal(this.faker.commerce().price());
        int quantity = this.faker.random().nextInt(1, 3);
        lineItem.setPrice(price);
        lineItem.setQuantity(quantity);

        total = total.add(price.multiply(BigDecimal.valueOf(quantity)));

        orderLineItems.add(lineItem);
      }

      invoice.setOrderLineItems(orderLineItems);
      invoice.setTotal(total);

      int paymentFaker = this.faker.random().nextInt(1, 3);
      PaymentInfo paymentInfo = new PaymentInfo();

      if (paymentFaker == 1) {
        paymentInfo.setPaymentType("CASH");
      } else if (paymentFaker == 2) {
        paymentInfo.setPaymentType("CREDIT_CARD");
        Business business = this.faker.business();
        paymentInfo.setCreditCardType(business.creditCardType());
        paymentInfo.setCreditCardNumber(business.creditCardNumber());
        paymentInfo.setCreditCardExpiry(business.creditCardExpiry());
      } else {
        paymentInfo.setPaymentType("UPI");
        paymentInfo.setUpiId(name.split(" ")[0].toLowerCase() + "@okaxis");
      }

      invoice.setPaymentInfo(paymentInfo);

      log.info("Mock invoice generated " + invoice);

      producer.send(
          new ProducerRecord<>(
              this.invoiceTopic,
              invoice.getInvoiceId(),
              invoice
          ), (result, e) -> {
            if (e != null) {
              log.error("Error while sending message to topic " +  this.invoiceTopic, e);
            }
          });
    }
  }

}