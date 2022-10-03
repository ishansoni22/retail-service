package com.ishan.retailservice.invoicefanout.port.adapters.config;

import java.util.Map;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

@Configuration
public class KafkaStreamsConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String brokerUrls;

  @Value("${app.name}")
  private String app;

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration kafkaStreamsConfiguration() {

    return new KafkaStreamsConfiguration(
        Map.of(
            StreamsConfig.APPLICATION_ID_CONFIG, app,
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrls,

            /**
             * Define the Application Server Config: This is utilised by Kafka to manage the instances
             * of the same group deployed to different instances across regions, or sites. It should be
             * unique for each instance. Kafka maintains the list of instances to track the
             * consumer threads.
             */

            StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:9092"
        )
    );
  }

}
