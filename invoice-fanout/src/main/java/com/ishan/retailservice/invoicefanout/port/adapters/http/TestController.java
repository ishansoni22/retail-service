package com.ishan.retailservice.invoicefanout.port.adapters.http;

import com.ishan.retailservice.invoicefanout.domain.LoyaltyPurchase;
import com.ishan.retailservice.invoicefanout.port.adapters.InteractiveQuery;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/test")
@Slf4j
public class TestController {

  @Autowired
  private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

  @Value("${loyalty.store}")
  private String loyaltyStore;

  @GetMapping("/customers/{customerId}")
  public ResponseEntity<?> getCustomerLoyalty(@PathVariable("customerId") String customerId) {
    KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();

    if (kafkaStreams.state().name().equals(State.RUNNING.name())) {
      LoyaltyPurchase customerLoyalty = new InteractiveQuery<LoyaltyPurchase>()
          .get(kafkaStreams, loyaltyStore, customerId);
      return ResponseEntity.ok(customerLoyalty);
    }

    log.info("Kafka Streams is not in a running state. Please try again later..." );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @GetMapping("/customers/")
  public ResponseEntity<?> getLoyaltyForAllCustomers() {
    KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();

    if (kafkaStreams.state().name().equals(State.RUNNING.name())) {
      List<KeyValue<String, List<LoyaltyPurchase>>> loyalties = new InteractiveQuery<List<LoyaltyPurchase>>()
          .getAll(kafkaStreams, loyaltyStore);

      return ResponseEntity.ok(loyalties);
    }

    log.info("Kafka Streams is not in a running state. Please try again later..." );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

}