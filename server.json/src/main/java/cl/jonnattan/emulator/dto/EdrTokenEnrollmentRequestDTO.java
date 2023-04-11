package cl.jonnattan.emulator.dto;

import lombok.Data;

@Data
public class EdrTokenEnrollmentRequestDTO {
	private EdrTokenEnrollmentRequestDTO.PartnerInfo partnerInfo;
	private EdrTokenEnrollmentRequestDTO.RequestorInfo requestorInfo;
	private EdrTokenEnrollmentRequestDTO.CardInfo cardInfo;
	private EdrTokenEnrollmentRequestDTO.TokenInfo tokenInfo;

	@Data
	public static class PartnerInfo {
		private String partnerId;
	}

	@Data
	public static class RequestorInfo {
		private String rid;
	}

	@Data
	public static class CardInfo {
		private String entry;
		private ProfileData data;
	}

	@Data
	public static class ProfileData {
		private String profile;
	}

	@Data
	public static class TokenInfo {
		private Presentation presentation;
	}

	@lombok.Data
	public static class Presentation {
		private String mode;
	}
}
