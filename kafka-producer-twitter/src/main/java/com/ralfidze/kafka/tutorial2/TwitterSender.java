package com.ralfidze.kafka.tutorial2;

import org.apache.kafka.clients.producer.KafkaProducer;

public interface TwitterSender<K, V> {
    void send(KafkaProducer<K,V> producer);
}
