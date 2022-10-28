package cl.ionix.emulator.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommerceMailResponseDTO {
	private CommerceMailResponseDTO.MetaMailDTO meta = new CommerceMailResponseDTO.MetaMailDTO();
	
	@Data
	public static class MetaMailDTO {
		private String status = "OK";
		private CommerceMailResponseDTO.DataMetaMailDTO data;
		
		public MetaMailDTO() {
			data = new CommerceMailResponseDTO.DataMetaMailDTO();
			data.getIdentifier().add("Desde emulador");
		}
	}
	
	@Data
	public static class DataMetaMailDTO {
		private List<String> identifier = new ArrayList<>();

		public List<String> getIdentifier() {
			return identifier;
		}

		public void setIdentifier(List<String> identifier) {
			this.identifier = identifier;
		}
	}
}
