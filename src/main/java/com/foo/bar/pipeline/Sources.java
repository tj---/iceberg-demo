package com.foo.bar.pipeline;

import java.util.Arrays;

import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import com.foo.bar.dto.InputMessage;
import com.foo.bar.schema.kafka.InputMessageDeserializer;

public class Sources {

	public KafkaSource<InputMessage> kafkaSourceConsumer(String inTopic, String kafkaBroker, String consumerGroup) {
	 
		return KafkaSource.<InputMessage>builder()
	     .setBootstrapServers(kafkaBroker)
	     .setGroupId(consumerGroup)
	     .setTopics(Arrays.asList(inTopic))
	     .setDeserializer(new InputMessageDeserializer())
	     .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
	     .build();
	}
}
