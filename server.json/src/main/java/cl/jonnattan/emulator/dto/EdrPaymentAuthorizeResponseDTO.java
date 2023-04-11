package cl.jonnattan.emulator.dto;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrPaymentAuthorizeResponseDTO implements IEmulator{
	private RequestorInfoAuth requestorInfo;
	private Transaction transaction;
	private EdrPaymentAuthorizeResponseDTO.Transactionex transactionex;

	@Data
	public static class Transactionex {
		private String authnumber;
		private String responsecode;
		private String availablebalance;
		private String status;
	}
}
