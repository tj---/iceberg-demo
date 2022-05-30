package com.foo.bar.schema.kafka;

import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.bar.dto.OutMessage;

public class OutMessageSerializer implements KafkaRecordSerializationSchema<OutMessage> {

	private static final long serialVersionUID = 3282426065274156528L;
	private final String topic;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public OutMessageSerializer(String topic) {
		this.topic = topic;
	}
	
	@Override
	public ProducerRecord<byte[], byte[]> serialize(OutMessage element, KafkaSinkContext context, Long timestamp) {
		try {
			byte[] key = objectMapper.writeValueAsBytes(element.getId());
			byte[] value = objectMapper.writeValueAsBytes(element);
			return new ProducerRecord<byte[], byte[]>(topic, key, value);
		}
		catch(JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
