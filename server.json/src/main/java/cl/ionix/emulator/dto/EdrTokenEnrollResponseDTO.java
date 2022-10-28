package cl.ionix.emulator.dto;

import cl.ionix.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrTokenEnrollResponseDTO implements IEmulator{
	private String reference;
	private CardInfo cardInfo;
	private EdrTokenEnrollResponseDTO.TokenInfo tokenInfo;

	@Data
	public static class TokenInfo {
		private String href;
		private String status;
		private String reason;
		private String reference;
	}

}
