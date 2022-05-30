package com.foo.bar.sample.producer;

import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.bar.dto.InputMessage;
import com.foo.bar.pipeline.Driver.Configurations;

public class SampleProducer {
	
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  
    private static final String[] COUNTRY_DIMENSION = new String[] { "IN", "US", "IT", "GE", "FR" };
    private static final String[] STATE_DIMENSION = new String[] { "ST1", "ST2", "ST3", "ST4", "ST5" };

    private static final KafkaProducer<String, String> PRODUCER;
    static {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configurations.KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        PRODUCER = new KafkaProducer<>(props);
    }

    private InputMessage record(int id) {
        String country = COUNTRY_DIMENSION[id % COUNTRY_DIMENSION.length];
        String state = STATE_DIMENSION[id % STATE_DIMENSION.length];
        InputMessage record = new InputMessage(id, country, state);
        record.setCol1(RandomStringUtils.randomAlphanumeric(10));
        record.setCol2(RandomStringUtils.randomAlphanumeric(10));

        return record;
    }

    public void produce(int id, String topic) throws JsonProcessingException {
    	InputMessage dataRecord = record(id);
        String serialized = OBJECT_MAPPER.writeValueAsString(dataRecord);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, Integer.toString(id), serialized);
        PRODUCER.send(record);
        PRODUCER.flush();
    }

}
