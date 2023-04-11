package cl.jonnattan.emulator.dto;

import java.util.List;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrTokenLogonResponseDTO implements IEmulator{
	String accessToken;
	List<Bin> tokenbins;

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
}
