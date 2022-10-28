package cl.ionix.emulator.dto;

import java.util.List;

import cl.ionix.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrPaymentLogonResponseDTO implements IEmulator{
	private String accessToken;
	private List<Bin> tokenbins;

	@Data
	public static class Bin {
		private String binEnd;
		private String binStart;
		private String country;
		private String product;
		private Enrollment enrollment;
		private List<Tsp> tsps;
	}

	@Data
	public static class Enrollment {
		private List<String> params;
	}

	@Data
	public static class Tsp {
		private String id;
		private int priority;
	}
}
