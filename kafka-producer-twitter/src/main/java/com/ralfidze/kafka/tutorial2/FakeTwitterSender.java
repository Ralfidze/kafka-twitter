package com.ralfidze.kafka.tutorial2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ralfidze.kafka.tutorial2.helpers.FakeTweetDto;
import com.ralfidze.kafka.tutorial2.helpers.TweetGenerator;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeTwitterSender implements TwitterSender<String, String> {
    private Logger logger = LoggerFactory.getLogger(FakeTwitterSender.class.getName());
    private int maxFakeTweetsCount = 100_000;

    @Override
    public void send(KafkaProducer<String, String> producer) {
        while (maxFakeTweetsCount > 0) {
            TweetGenerator tweetGenerator = new TweetGenerator();
            FakeTweetDto tweet = tweetGenerator.generate();
            ObjectMapper mapper = new ObjectMapper();
            String message = null;
            try {
                message = mapper.writeValueAsString(tweet);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (message != null) {
                logger.info(message);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                producer.send(new ProducerRecord<>("twitter_tweets", null, message), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (e != null) {
                            logger.error("Something bad happened!", e);
                        }
                    }
                });
            }
            maxFakeTweetsCount--;
        }
    }
}
