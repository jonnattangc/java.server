package cl.jonnattan.emulator.dto;

import lombok.Data;

@Data
public class AppConfigurationRequestDTO {
	private String endPoint;
	private Boolean error;
	private Integer type;
	private String code;
	private String message;
}
