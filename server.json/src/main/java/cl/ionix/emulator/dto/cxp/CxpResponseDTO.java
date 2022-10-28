package cl.ionix.emulator.dto.cxp;

import lombok.Data;

@Data
public class CxpResponseDTO implements ICxpResponse {

	private static final long serialVersionUID = 1L;

	private Object data;
	private Integer statusCode;
	private String statusDescription;
	private String errors;

}
