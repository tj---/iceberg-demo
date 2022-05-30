package com.foo.bar.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@ToString
public class InputMessage {
	private int id;
	private String col1;
	private String col2;
	private String xing;
	private String bing;
	
	public InputMessage(int id) {
		this.id = id;
	}
	
	public InputMessage(int id, String country, String state) {
		this.id = id;
		this.xing = country;
		this.bing = state;
	}
}
