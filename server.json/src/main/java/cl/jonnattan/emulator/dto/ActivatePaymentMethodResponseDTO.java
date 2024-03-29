package cl.jonnattan.emulator.dto;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class ActivatePaymentMethodResponseDTO  implements IEmulator{
	private ActivatePaymentMethodResponseDTO.InfoData data;
	private ActivatePaymentMethodResponseDTO.InfoMeta meta;

	@Data
	public static class InfoData {
		private boolean status;
	}

	@Data
	public static class InfoMeta {
		private String[] messages;
		private String status;
	}
}
