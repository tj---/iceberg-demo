package com.foo.bar.mapper;

import org.apache.flink.api.common.functions.MapFunction;

import com.foo.bar.dto.InputMessage;
import com.foo.bar.dto.OutMessage;

public class MessageMapper implements MapFunction<InputMessage, OutMessage> {

	private static final long serialVersionUID = 137604945116083709L;

	@Override
	public OutMessage map(InputMessage value) throws Exception {
		return OutMessage.builder()
			.id(value.getId() * 100)
			.name(value.getCol1())
			.known(value.getCol2())
			.country(value.getXing())
			.fact(value.getBing())
			.build();
	}
	
	/**
	 * Joins two messages
	 */
	public static InputMessage join(InputMessage message1, InputMessage message2) {
		System.out.println("Joining record with id: " + message1.getId() + " to record with id: " + message2.getId());
		InputMessage joined = new InputMessage(message1.getId());
		joined.setCol1(message1.getCol1() + "_" + message2.getCol1());
		joined.setCol2(message1.getCol2() + "_" + message2.getCol2());
		joined.setBing(message1.getBing() + "_" + message2.getBing());
		joined.setXing(message1.getXing() + "_" + message2.getXing());
		
		return joined;
	}

}
