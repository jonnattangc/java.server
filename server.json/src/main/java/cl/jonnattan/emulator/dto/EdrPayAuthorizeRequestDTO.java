package cl.jonnattan.emulator.dto;

import lombok.Getter;

@Getter
public class EdrPayAuthorizeRequestDTO {
	private RequestorInfoAuth requestorInfo;
	private Token token;
	private Transaction transaction;
	private Transactionex transactionex;

	@Getter
	public static class Transactionex {
		private String mcc;
	}

	@Getter
	public static class Token {
		private String data;
		private String trid;
		private String presentation;
	}
}
