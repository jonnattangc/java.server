package cl.ionix.emulator.utils;

public class EmulatorException extends Exception {

	private static final long serialVersionUID = 1L;
	private final String code;

	public EmulatorException(String msg) {
		super(msg);
		this.code = "-1";
	}

	public EmulatorException(String msg, String code) {
		super(msg);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
