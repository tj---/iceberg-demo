package com.foo.bar.pipeline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.types.Row;
import org.apache.hadoop.conf.Configuration;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.CatalogLoader;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.sink.FlinkSink;

import com.foo.bar.dto.OutMessage;
import com.foo.bar.pipeline.Driver.Configurations;
import com.foo.bar.schema.kafka.OutMessageSerializer;

public class Sinks {
	
	/**
	 * Sink to Kafka topic
	 */
	public KafkaSink<OutMessage> createKafkaSink(String outTopic, String kafkaBroker) {
		return KafkaSink.<OutMessage>builder()
	        .setBootstrapServers(kafkaBroker)
	        .setRecordSerializer(new OutMessageSerializer(outTopic))
	        .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
	        .setTransactionalIdPrefix("tx-prefix-")
	        .build();
	}
	
	
	/**
	 * Sink to Iceberg Table. The Metastore access is via Thrift protocol
	 * The underlying data lake is GCS (on GCP)
	 */
	
	public void prepareIcebergSink(DataStream<Row> rowStream) {
		
	    String HIVE_CATALOG = "iceberg";
		CatalogLoader catalogLoader = CatalogLoader.hive(HIVE_CATALOG, hadoopConfigs(), hiveProperties());
		TableLoader tableLoader = TableLoader.fromCatalog(catalogLoader, TableIdentifier.parse(namespacedIcebergTable()));
		
		FlinkSink.forRow(rowStream, TARGET_TABLE_SCHEMA)
        .tableLoader(tableLoader)
        .writeParallelism(1)
        .equalityFieldColumns(Arrays.asList("name"))
        .upsert(true)
        .append();
	}
	
	private String namespacedIcebergTable() {
		return Configurations.ICEBERRG_CATALOG + "." + Configurations.ICEBERRG_TABLE;
	}
	
	private static final TableSchema TARGET_TABLE_SCHEMA = TableSchema.builder()
		.field("id", DataTypes.BIGINT())
		.field("name", DataTypes.STRING())
		.field("known", DataTypes.STRING())
		.field("country", DataTypes.STRING())
		.field("fact", DataTypes.STRING())
		.build();
		
	
	private Map<String, String> hiveProperties() {
		Map<String, String> properties = new HashMap<>();
		
	    properties.put("type", "iceberg");
	    properties.put("connector", "iceberg");
	    properties.put("catalog-name", "iceberg");
	    
	    properties.put("catalog-type", "hive");
	    
	    // Adding it here as well
	    properties.put("catalog-database", Configurations.ICEBERRG_CATALOG);
	    properties.put("catalog-table", Configurations.ICEBERRG_TABLE);
	    
	    properties.put("clients", "5");
	    properties.put("property-version", "2");
	    properties.put("warehouse", Configurations.GCP_TABLE_PATH);
	    
	    // The most relevant attribute
	    properties.put("uri", Configurations.THRIFT_URI);
	    
	    return properties;
	}
	
	private Configuration hadoopConfigs() {
		Configuration config = new Configuration();
	    
		config.set("fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem");
		config.set("fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS");
		config.set("fs.gs.project.id", Configurations.GCP_PROJECT_ID);
		config.set("fs.gs.auth.type", "SERVICE_ACCOUNT_JSON_KEYFILE");
		config.set("fs.gs.auth.service.account.json.keyfile", Configurations.GCP_SERVICE_ACCOUNT_KEYFILE_PATH);
		config.set("fs.gs.reported.permissions", "777");
		config.set("fs.gs.http.max.retry", "2");
		config.set("fs.gs.http.read-timeout", "50000");
		
		return config;
	}

}
