package com.foo.bar.sample.producer;

import java.util.concurrent.ThreadLocalRandom;

import com.foo.bar.dto.InputMessage;
import com.foo.bar.pipeline.Driver.Configurations;

/**
 * Generates 20 random samples {@link InputMessage} and pushes them to two Kafka topics
 */
public class SampleGenerator {
	
	private static final SampleProducer DATA_PRODUCER = new SampleProducer();

    public static void main(String[] args) throws Exception {
    	int start = ThreadLocalRandom.current().nextInt();
    	int end = start + 20;
        for(int index = start; index <= end; index++) {
            DATA_PRODUCER.produce(index, Configurations.KAFKA_INPUT_TOPIC_1);
            DATA_PRODUCER.produce(index, Configurations.KAFKA_INPUT_TOPIC_2);
            System.out.println("Created records for id: " + index);
            Thread.sleep(100);
        }
    }
}
