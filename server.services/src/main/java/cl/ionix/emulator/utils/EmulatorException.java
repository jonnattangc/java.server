package cl.ionix.emulator.utils;

public class EmulatorException extends Exception {

	private static final long serialVersionUID = 1L;
	private String code = "-1";

	public EmulatorException(String msg) {
		super(msg);
	}

	public EmulatorException(String msg, String code) {
		super(msg);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
