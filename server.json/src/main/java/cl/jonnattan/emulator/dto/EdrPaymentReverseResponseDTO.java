package cl.jonnattan.emulator.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrPaymentReverseResponseDTO implements IEmulator{
	private RequestorInfoReverse requestorInfo;
	@JsonAlias("void")
	private TransactionVoid transactionVoid;
	private EdrPaymentReverseResponseDTO.Transactionex transactionex;

	@Data
	public static class Transactionex {
		private String mcc;
		private String authnumber;
		private String responsecode;
		private String availablebalance;
		private String status;
	}
}
