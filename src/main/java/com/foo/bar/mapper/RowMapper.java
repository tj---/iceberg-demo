package com.foo.bar.mapper;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;
import org.apache.iceberg.flink.sink.FlinkSink;

import com.foo.bar.dto.OutMessage;

/**
 * A mapper to convert message to something specific ({@link Row}) to make it understandable by Iceberg's ({@link FlinkSink})
 */
public class RowMapper implements MapFunction<OutMessage, Row> {

	private static final long serialVersionUID = 1211146343730956104L;

	@Override
	public Row map(OutMessage value) throws Exception {

		return Row.ofKind(RowKind.INSERT, 
				value.getId(),
				value.getName(),
				value.getKnown(),
				value.getCountry(),
				value.getFact()
			);
	}

}
