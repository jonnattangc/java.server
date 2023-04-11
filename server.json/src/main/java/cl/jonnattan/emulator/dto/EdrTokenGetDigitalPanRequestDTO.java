package cl.jonnattan.emulator.dto;

import lombok.Data;

@Data
public class EdrTokenGetDigitalPanRequestDTO {

	private EdrTokenGetDigitalPanRequestDTO.RequestorInfo requestorInfo;
	private EdrTokenGetDigitalPanRequestDTO.CardInfo cardInfo;

	@Data
	public static class RequestorInfo {
		private String rid;
	}

	@Data
	public static class CardInfo {
		private EdrTokenGetDigitalPanRequestDTO.ProfileData data;
	}

	@Data
	public static class ProfileData {
		private String profile;
	}
}
