package com.foo.bar.schema.kafka;

import java.io.IOException;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.bar.dto.InputMessage;

public class InputMessageDeserializer implements KafkaRecordDeserializationSchema<InputMessage> {
	
	private static final long serialVersionUID = 4384600592368810457L;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public TypeInformation<InputMessage> getProducedType() {
		return TypeInformation.of(InputMessage.class);
	}

	@Override
	public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<InputMessage> out) throws IOException {
		InputMessage message = objectMapper.readValue(record.value(), InputMessage.class);
		out.collect(message);
	}
}
