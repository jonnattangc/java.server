package cl.jonnattan.emulator.dto;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrPaymentCreateCryptogramResponseDTO implements IEmulator{

	private TokenInfo tokenInfo;

	@Data
	public static class TokenInfo {
		private String reference;
		private String status;
		private CripData data;
	}

	@Data
	public static class CripData {
		private String criptogram;
		private String atc;
	}
}
