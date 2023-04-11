package cl.jonnattan.emulator.utils;

import org.springframework.http.HttpStatus;

import cl.jonnattan.emulator.enums.TypeResponse;

public class ConfException extends Exception {

	private static final long serialVersionUID = 1L;
	private String code = "-1";
	private TypeResponse type = TypeResponse.HTTP_RESPONSE_200;

	public ConfException(String msg) {
		super(msg);
	}

	public ConfException(String msg, String code) {
		super(msg);
		this.code = code;
	}

	public ConfException(String msg, String code, TypeResponse type) {
		super(msg);
		this.code = code;
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TypeResponse getType() {
		return type;
	}

	public void setType(TypeResponse type) {
		this.type = type;
	}

	public HttpStatus getStatus() {
		HttpStatus status = null;
		switch (type) {
		case HTTP_RESPONSE_200:
			status = HttpStatus.OK;
			break;
		case HTTP_RESPONSE_400:
			status = HttpStatus.BAD_REQUEST;
			break;
		case HTTP_RESPONSE_409:
			status = HttpStatus.CONFLICT;
			break;
		case HTTP_RESPONSE_500:
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			break;
		case HTTP_RESPONSE_503:
			status = HttpStatus.SERVICE_UNAVAILABLE;
			break;
		default:
			status = HttpStatus.OK;
		}
		return status;
	}
}
