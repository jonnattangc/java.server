package cl.jonnattan.emulator.dto;

import java.util.List;

import lombok.Data;

@Data
public class EdrPaymentLogonRequestDTO {
	String requestInfo;
	DomainInfo domainInfo;
	List<Bin> bins;
	PartnerInfo partnerInfo;
	Server server;
	RequestorInfo requestorInfo;

	@Data
	public static class DomainInfo {
		String domainName;
		String paymentDomain;
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
		String product;
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

		String partnerNumber;
		String partnerId;
		String secret;
		String networkCode;
		String clearanceMode;
	}

	@Data
	public static class Server {

		List<Certificate> certificates;
	}

	@Data
	public static class Certificate {

		String alias;
		String content;
		String usage;
	}

	@Data
	public static class RequestorInfo {

		String requestorId;
		String secret;
	}
}
