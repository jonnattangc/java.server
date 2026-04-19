package cl.jonnattan.emulator.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cl.jonnattan.emulator.enums.TypeResponse;

class ExceptionsTest {

	@Test
	void emulatorException_constructorSoloMensaje_codeDefecto() {
		EmulatorException ex = new EmulatorException("err");
		assertEquals("err", ex.getMessage());
		assertEquals("-1", ex.getCode());
	}

	@Test
	void emulatorException_constructorConCode() {
		EmulatorException ex = new EmulatorException("err", "500");
		assertEquals("500", ex.getCode());
	}

	@Test
	void confException_constructorBasico_defaults() {
		ConfException ex = new ConfException("err");
		assertEquals("-1", ex.getCode());
		assertEquals(TypeResponse.HTTP_RESPONSE_200, ex.getType());
		assertEquals(HttpStatus.OK, ex.getStatus());
	}

	@Test
	void confException_setters_actualizanValores() {
		ConfException ex = new ConfException("err");
		ex.setCode("42");
		ex.setType(TypeResponse.HTTP_RESPONSE_500);
		assertEquals("42", ex.getCode());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
	}

	@Test
	void confException_getStatus_mapeaTodosLosTipos() {
		assertEquals(HttpStatus.OK, mapStatus(TypeResponse.HTTP_RESPONSE_200));
		assertEquals(HttpStatus.BAD_REQUEST, mapStatus(TypeResponse.HTTP_RESPONSE_400));
		assertEquals(HttpStatus.CONFLICT, mapStatus(TypeResponse.HTTP_RESPONSE_409));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, mapStatus(TypeResponse.HTTP_RESPONSE_500));
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, mapStatus(TypeResponse.HTTP_RESPONSE_503));
	}

	private HttpStatus mapStatus(TypeResponse type) {
		ConfException ex = new ConfException("m", "c", type);
		return ex.getStatus();
	}

	@Test
	void confException_constructorCompleto_asignaTodo() {
		ConfException ex = new ConfException("m", "c", TypeResponse.HTTP_RESPONSE_400);
		assertEquals("c", ex.getCode());
		assertEquals(TypeResponse.HTTP_RESPONSE_400, ex.getType());
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}
}
