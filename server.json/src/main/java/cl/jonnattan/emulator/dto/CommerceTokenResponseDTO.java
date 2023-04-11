package cl.jonnattan.emulator.dto;

import lombok.Data;

@Data
public class CommerceTokenResponseDTO {
	private Integer expires_in;
	private String access_token;
	private  String token_type;
}
