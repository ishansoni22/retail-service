package com.ishan.retailservice.invoicefanout.domain;

import java.math.MathContext;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
@Deprecated
public class LoyaltyValueTransformer implements ValueTransformer<Invoice, LoyaltyPurchase> {

  private String loyaltyStateStore;
  private KeyValueStore<String, Integer> store;
  
  public LoyaltyValueTransformer(String loyaltyStateStore) {
    this.loyaltyStateStore = loyaltyStateStore;
  }
  
  @Override
  public void init(ProcessorContext processorContext) {
    this.store = processorContext
        .getStateStore(this.loyaltyStateStore);
  }

  @Override
  public LoyaltyPurchase transform(Invoice invoice) {
    String customerId = invoice.getCustomerId();
    LoyaltyPurchase loyaltyPurchase = new LoyaltyPurchase();
    loyaltyPurchase.setInvoiceId(invoice.getInvoiceId());
    loyaltyPurchase.setCustomerId(customerId);
    loyaltyPurchase.setCustomerName(invoice.getCustomerName());
    loyaltyPurchase.setPurchaseValue(invoice.getTotal());

    int earnedPoints =
        invoice.getTotal().round(new MathContext(0, RoundingMode.FLOOR)).intValue();

    Integer totalPoints = this.store.get(customerId);

    if (totalPoints != null) {
      totalPoints = totalPoints + earnedPoints;
      log.info("Repeat purchase by customer = " + customerId + ", Earned points = " + earnedPoints + ", total points = " + totalPoints);
    } else {
      totalPoints = earnedPoints;
    }

    this.store.put(customerId, totalPoints);

    loyaltyPurchase.setLoyaltyPoints(earnedPoints);
    loyaltyPurchase.setTotalLoyaltyPoints(totalPoints);

    return loyaltyPurchase;
  }

  @Override
  public void close() {
  }

}
