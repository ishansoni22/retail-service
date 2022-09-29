package com.ishan.retailservice.invoicefanout.port.adapters.config;

import com.ishan.retailservice.invoicefanout.domain.Invoice;
import com.ishan.retailservice.invoicefanout.domain.LoyaltyPurchase;
import com.ishan.retailservice.invoicefanout.domain.ProductPurchase;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class AppSerdes extends Serdes {

  static final class InvoiceSerde extends Serdes.WrapperSerde<Invoice> {

    InvoiceSerde() {
      super(new JsonSerializer<>(), new JsonDeserializer<>());
    }
  }

  public static Serde<Invoice> invoice() {
    InvoiceSerde invoiceSerde = new InvoiceSerde();
    Map<String, Object> configMap = new HashMap<>();
    configMap.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, Invoice.class);
    invoiceSerde.configure(configMap, false);
    return invoiceSerde;
  }

  static final class LoyaltyPurchaseSerde extends Serdes.WrapperSerde<LoyaltyPurchase> {

    LoyaltyPurchaseSerde() {
      super(new JsonSerializer<>(), new JsonDeserializer<>());
    }
  }

  public static Serde<LoyaltyPurchase> loyaltyPurchase() {
    LoyaltyPurchaseSerde loyaltyPurchaseSerde = new LoyaltyPurchaseSerde();
    Map<String, Object> configMap = new HashMap<>();
    configMap.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, LoyaltyPurchase.class);
    loyaltyPurchaseSerde.configure(configMap, false);
    return loyaltyPurchaseSerde;
  }

  static final class ProductPurchaseSerde extends Serdes.WrapperSerde<ProductPurchase> {

    ProductPurchaseSerde() {
      super(new JsonSerializer<>(), new JsonDeserializer<>());
    }
  }

  public static Serde<ProductPurchase> productPurchase() {
    ProductPurchaseSerde productPurchaseSerde = new ProductPurchaseSerde();
    Map<String, Object> configMap = new HashMap<>();
    configMap.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, LoyaltyPurchase.class);
    productPurchaseSerde.configure(configMap, false);
    return productPurchaseSerde;
  }

}