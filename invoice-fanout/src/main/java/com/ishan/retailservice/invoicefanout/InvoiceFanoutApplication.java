package com.ishan.retailservice.invoicefanout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
public class InvoiceFanoutApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceFanoutApplication.class, args);
	}

}
