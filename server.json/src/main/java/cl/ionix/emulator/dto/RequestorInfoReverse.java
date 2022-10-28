package cl.ionix.emulator.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class RequestorInfoReverse {
	@JsonAlias("rId")
	private String rId;
	private String requestorId;
	private String secret;
}
