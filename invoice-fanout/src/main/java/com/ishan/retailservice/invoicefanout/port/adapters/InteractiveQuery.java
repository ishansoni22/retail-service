package com.ishan.retailservice.invoicefanout.port.adapters;

import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

public class InteractiveQuery<T> {

  public T get(
      KafkaStreams kafkaStreams,
      String storeName,
      String key
      ) {

    ReadOnlyKeyValueStore<String, T> store = kafkaStreams.store(
        StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));

    return store.get(key);
  }

  public List<KeyValue<String, T>> getAll(
      KafkaStreams kafkaStreams,
      String storeName
  ) {

    List<KeyValue<String, T>> results = new ArrayList<>();
    ReadOnlyKeyValueStore<String, T> store = kafkaStreams.store(
        StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));

    store.all().forEachRemaining(
        keyValue -> results.add(new KeyValue<>(keyValue.key,  keyValue.value)));

    return results;

  }

}
