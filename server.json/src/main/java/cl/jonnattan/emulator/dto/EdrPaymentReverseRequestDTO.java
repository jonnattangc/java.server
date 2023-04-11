package cl.jonnattan.emulator.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class EdrPaymentReverseRequestDTO {
	private RequestorInfoReverse requestorInfo;
	private PaymentToken token;
	@JsonAlias("void")
	private TransactionVoid transactionVoid;
	
	private TransactionVoid originalPurchase;

	@Data
	public static class PaymentToken {
		private String trid;
		private String presentation;
		private String data;
	}

}
