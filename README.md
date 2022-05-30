#### Introduction
[Apache Iceberg](https://iceberg.apache.org/) is an advanced table specification for high volume, fast analytical tables. It can be interacted with a large number of tools (both on the Read and Write ends). It is an improvement to the first generation table format Apache Hive and is siblings with the contemporary [Apache Hudi and Delta lake standards](https://www.dremio.com/subsurface/comparison-of-data-lake-table-formats-iceberg-hudi-and-delta-lake/).

These are some of the features of Apache Iceberg table format:
- ACID transaction
- Hidden partitions and partition evolution
- Schema evolution
- Time travel

This article [Building an Enterprise-Level Real-Time Data Lake Based on Flink and Iceberg](https://alibaba-cloud.medium.com/building-an-enterprise-level-real-time-data-lake-based-on-flink-and-iceberg-6ea2f26c8a00) on Alibaba Engineering, talks in detail about the architectural patterns that may be realized to achieve a functioning Real time Data Lake

#### Real time Delta Lake
In this example, data is made available from Kafka in a Trino Iceberg table in real-time via Flink stream processing.

![Kafka Flink Iceberg Trino](https://drive.google.com/file/d/1Kxlnn0SAQ7hqr5WJVz3kpc14hyTzwre7/view?usp=sharing)


These are the layers used in this demo:
1. Object Store / File System: [Google Cloud storage](https://cloud.google.com/storage).
2. Streaming Source: Kafka. The data can be written to Kafka by some "other origin" source. For example, in this example, data is being written to Kafka in a long running for loop
3. **Stream processing system**: [Apache Flink](https://flink.apache.org/)
4. Streaming Targets: Two target sinks are used: `Kafka` and `Iceberg table` (The writes to the Iceberg table happen via the [Hadoop GCS connector](https://cloud.google.com/dataproc/docs/concepts/connectors/cloud-storage#non-dataproc_clusters) - shaded jar is preferred to avoid dependency versioning mess)
5. **Query Engine**: Trino (via [Iceberg connector](https://trino.io/docs/current/connector/iceberg.html) on [GCS](https://trino.io/docs/current/connector/hive.html#google-cloud-storage-configuration))


#### Prerequisites
1. Setup Kafka - For local development, Kafka may be made available via a [docker container](https://hub.docker.com/r/wurstmeister/kafka/).
2. Setup Trino with the necessary connectors - Clone [tj---/trino-hive](https://github.com/tj---/trino-hive) repository and follow these [steps](https://github.com/tj---/trino-hive#steps) to launch the necessary containers.
3. Create an Iceberg table via Trino: Follow [these steps](https://github.com/tj---/trino-hive#2-iceberg) to create an Iceberg table.
4. Note all of the parameters from the steps above and update the constants in the `Driver.Configurations` class.

#### Running the Demo
1. Run Driver::Main to setup and initiate the Flink pipeline. Make sure that there are no errors. Once ready, the pipeline becomes ready to process the data.
2. Run SampleGenerator::Main that ingests records to Kafka source topics. Within a short time, based on the configured Flink [check-point](https://nightlies.apache.org/flink/flink-docs-master/docs/ops/state/checkpoints/), data becomes available in the table
3. Run TrinoIcebergReader::Main to read data from the sample table via the Trino engine.
