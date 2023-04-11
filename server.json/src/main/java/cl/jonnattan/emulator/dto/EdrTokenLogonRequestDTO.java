package cl.jonnattan.emulator.dto;

import java.util.List;

import lombok.Data;

@Data
public class EdrTokenLogonRequestDTO {
	String requestInfo;
	PartnerInfo partnerInfo;
	DomainInfo domainInfo;
	List<Bin> bins;

	@Data
	public static class DomainInfo {
		String domainName;
		String tokenDomain;
		DomainRestrictions domainRestrictions;
	}

	@Data
	public static class DomainRestrictions {
		Provisioning provisioning;
		Usage usage;
		String reference;
	}

	@Data
	public static class Provisioning {
		int maximumCredentials;
		int maximumTokens;
		List<String> presentationType;
	}

	@Data
	public static class Usage {
		int credentialsCount;
		int tokenCount;
	}

	@Data
	public static class Bin {
		String binEnd;
		String binStart;
		String country;
		Enrollment enrollment;
		List<Tsp> tsps;
	}

	@Data
	public static class Enrollment {
		List<String> params;
	}

	@Data
	public static class Tsp {
		String id;
		int priority;
	}

	@Data
	public static class PartnerInfo {
		String partnerId;
		String secret;
	}
}
