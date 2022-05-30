package com.foo.bar.pipeline;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;

import com.foo.bar.dto.InputMessage;
import com.foo.bar.dto.OutMessage;
import com.foo.bar.mapper.RowMapper;
import com.foo.bar.pipeline.Driver.Configurations;
import com.foo.bar.mapper.MessageMapper;

/**
 * This pipeline makes use of Flink's DataStream APIs to continuously process data in real-time.
 * It streams from two Kafka topics, joins the two streams to result in a 3rd stream
 * The result is then sinked into two Sinks
 * 	1. A Kafka Topic
 * 	2. An Iceberg Table
 * 
 * Prerequisites:
 * The target Iceberg table must have been created before the Streaming begins
 */
public class FlinkPipeline {
	
	public void compose(String inTopic1,  String inTopic2, String outTopic) throws Exception {
		
		// Set up a Flink local Stream execution environment
		StreamExecutionEnvironment environment = StreamExecutionEnvironment
			.getExecutionEnvironment()
			.enableCheckpointing(Configurations.CHECK_POINT_INTERVAL)
			.setParallelism(1)
			.setMaxParallelism(1)
		;
		
		// Streaming from the first Kafka topic inTopic1
		KafkaSource<InputMessage> kafkaSource1 = new Sources().kafkaSourceConsumer(inTopic1, Configurations.KAFKA_BOOTSTRAP_SERVERS, "consumer-1");
		DataStreamSource<InputMessage> inStream1 = environment.fromSource(kafkaSource1, WatermarkStrategy.forMonotonousTimestamps(), "KS-1");
		
		// Streaming from the second Kafka topic inTopic2
		KafkaSource<InputMessage> kafkaSource2 = new Sources().kafkaSourceConsumer(inTopic2, Configurations.KAFKA_BOOTSTRAP_SERVERS, "consumer-2");
		DataStreamSource<InputMessage> inStream2 = environment.fromSource(kafkaSource2, WatermarkStrategy.forMonotonousTimestamps(), "KS-2");
		
		// Join the two streams
		DataStream<InputMessage> joinedStream = inStream1.join(inStream2)
			.where(sampleMessage1 -> sampleMessage1.getId()).equalTo(sampleMessage2 -> sampleMessage2.getId())
			.window(TumblingEventTimeWindows.of(Time.of(3, TimeUnit.SECONDS)))
			.apply((first, second) -> MessageMapper.join(first, second));
		
		// Map the joined stream that results in a transformed message
		DataStream<OutMessage> mappedStream = joinedStream.map(new MessageMapper());
		
		// 1. Sink in a Kafka topic
		KafkaSink<OutMessage> kafkaSink = new Sinks().createKafkaSink(outTopic, Configurations.KAFKA_BOOTSTRAP_SERVERS);
		mappedStream.sinkTo(kafkaSink);
		
		// 2. Sink in an Iceberg Table
		DataStream<Row> rowStream = mappedStream.map(new RowMapper());
		new Sinks().prepareIcebergSink(rowStream);
		
		
		environment.execute("kafka-flink-iceberg pipeline");
	}

}
