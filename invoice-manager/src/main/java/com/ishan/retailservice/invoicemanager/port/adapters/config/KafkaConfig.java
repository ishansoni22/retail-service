package com.ishan.retailservice.invoicemanager.port.adapters.config;

import com.ishan.retailservice.invoicemanager.domain.Invoice;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String kafkaBootstrapUrl;

  @Value("${invoice.topic}")
  private String invoiceTopic;

  @Bean
  public NewTopic invoicesTopic() {
    return TopicBuilder
        .name(this.invoiceTopic)
        .partitions(10)
        .build();
  }

  public Map<String, Object> producerConfig() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrl);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return props;
  }

  @Bean
  public KafkaProducer<String, Invoice> invoiceProducer() {
    return new KafkaProducer<>(producerConfig());
  }

}
