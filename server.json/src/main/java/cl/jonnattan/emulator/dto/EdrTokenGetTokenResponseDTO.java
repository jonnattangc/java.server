package cl.jonnattan.emulator.dto;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrTokenGetTokenResponseDTO implements IEmulator{
	private EdrTokenGetTokenResponseDTO.TokenInfo tokeninfo;

	@Data
	public static class TokenInfo {
		private String reference;
		private String status;
		private Profile data;
	}

	@Data
	public static class Profile {
		private String profile;
	}
}
