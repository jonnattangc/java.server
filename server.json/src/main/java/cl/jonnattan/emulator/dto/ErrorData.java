package cl.jonnattan.emulator.dto;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class ErrorData implements IEmulator {

	private String code;
	private String message;

	public ErrorData( ) {
      this(null,null);
	}
	
	public ErrorData(String code, String msg) {
		this.code = code;
		this.message = msg;
	}
}
