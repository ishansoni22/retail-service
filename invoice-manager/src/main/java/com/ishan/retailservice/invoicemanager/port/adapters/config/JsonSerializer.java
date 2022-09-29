package com.ishan.retailservice.invoicemanager.port.adapters.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerializer<T> implements Serializer<T> {

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public byte[] serialize(String s, T data) {
    if (Objects.isNull(data)) {
      return null;
    }

    try {
      return this.mapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new SerializationException("Error while serializing object", e);
    }
  }

}
