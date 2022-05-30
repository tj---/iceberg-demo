package com.foo.bar.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class Driver {
	
    public static void main(String[] args) throws Exception {
		
    	setupLoggers();
		
        FlinkPipeline pipeline = new FlinkPipeline();
        pipeline.compose(Configurations.KAFKA_INPUT_TOPIC_1, Configurations.KAFKA_INPUT_TOPIC_2, Configurations.KAFKA_OUT_TOPIC);
    }
    
    private static void setupLoggers() {
    	Logger logger = LoggerFactory.getILoggerFactory().getLogger("org");
    	((ch.qos.logback.classic.Logger) logger).setLevel(Level.INFO);
    	
    	logger = LoggerFactory.getILoggerFactory().getLogger("com");
    	((ch.qos.logback.classic.Logger) logger).setLevel(Level.INFO);
    }
    
    public static class Configurations {
    	public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    	public static final String KAFKA_INPUT_TOPIC_1 = "input-p1";
    	public static final String KAFKA_INPUT_TOPIC_2 = "input-p2";
    	static final String KAFKA_OUT_TOPIC = "out-p";
    	
    	static final String ICEBERRG_CATALOG = "iceberg_gcs2";
    	static final String ICEBERRG_TABLE = "sample_table4";
    	static final String THRIFT_URI = "thrift://localhost:9083";
    	
    	static final String GCP_TABLE_PATH = "gs://sample_test_1090/sample_table4/";
    	static final String GCP_PROJECT_ID = "my-project-1541925960718";
    	static final String GCP_SERVICE_ACCOUNT_KEYFILE_PATH = "/Users/foo/temp/gcp-service-credentials.json";
    	
    	static final long CHECK_POINT_INTERVAL = 5_000;
    }
}