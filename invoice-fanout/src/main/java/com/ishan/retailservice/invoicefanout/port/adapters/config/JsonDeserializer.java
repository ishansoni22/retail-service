package com.ishan.retailservice.invoicefanout.port.adapters.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonDeserializer<T> implements Deserializer<T> {

  private ObjectMapper mapper;
  private Class<T> className;
  public static final String KEY_CLASS_NAME_CONFIG = "key.class.name";
  public static final String VALUE_CLASS_NAME_CONFIG = "value.class.name";

  public JsonDeserializer() {
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    if (isKey)
    this.className = (Class<T>) configs.get(KEY_CLASS_NAME_CONFIG);
        else
    this.className = (Class<T>) configs.get(VALUE_CLASS_NAME_CONFIG);
  }

  @Override
  public T deserialize(String s, byte[] data) {
    if (data == null) {
      return null;
    }
    try {
      return this.mapper.readValue(data, this.className);
    } catch (Exception e) {
      throw new SerializationException(e);
    }
  }

  @Override
  public void close() {

  }

}
