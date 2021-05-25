package kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoWithThread {
    public static void main(String[] args) {
        new ConsumerDemoWithThread().run();
    }

    private ConsumerDemoWithThread() {}

    private void run(){
        Logger logger = LoggerFactory.getLogger(ConsumerDemoWithThread.class);

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-sixth-application";
        String topic = "first_topic";

        //latch for dealing with multiple threads
        CountDownLatch latch = new CountDownLatch(1);

        Runnable myConsumerRunnnable = new ConsumerRunnable(
                bootstrapServers,
                groupId,
                topic,
                latch
        );

        Thread myThread = new Thread(myConsumerRunnnable);
        myThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            logger.info("Caught shutdown hook!");
            ((ConsumerRunnable) myConsumerRunnnable).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));


        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("App got interrupted", e);
        } finally {
            logger.info("Application is closing!");
        }
    }

    public class ConsumerRunnable implements Runnable {

        private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);

        private CountDownLatch latch;
        private KafkaConsumer<String, String> kafkaConsumer;

        public ConsumerRunnable(String bootstrapServers,
                              String groupId,
                              String topic,
                              CountDownLatch latch) {
            this.latch = latch;

            // create consumer configs
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // create consumer
            kafkaConsumer = new KafkaConsumer<String, String>(properties);
            // subscribe consumer to the topic
            kafkaConsumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run() {
            // poll for new data
            try {
                while (true) {
                    ConsumerRecords<String, String> records =
                            kafkaConsumer.poll(Duration.ofMillis(100)); // Duration.ofMillis() is new way in Kafka 2.0.0
                    for (ConsumerRecord record : records) {
                        logger.info("Key: " + record.key() + ", Value: " + record.value());
                        logger.info("Partition: " + record.partition() + " , Offset:" + record.offset());
                    }
                }
            } catch (WakeupException ex) {
                logger.info("Recieved shutdown signal!");
            } finally {
                kafkaConsumer.close();
                // tell our main code we are done with the consumer
                latch.countDown();
            }
        }

        public void shutdown(){
            // the wakeup() method is a special method to interrupt consumer.poll()
            // it will throw the exception WakeUpException
            kafkaConsumer.wakeup();
        }
    }
}
