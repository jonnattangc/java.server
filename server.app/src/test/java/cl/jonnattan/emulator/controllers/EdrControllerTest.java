package cl.jonnattan.emulator.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import cl.jonnattan.emulator.enums.TypeResponse;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.interfaces.IEdr;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.EmulatorException;

@ExtendWith(MockitoExtension.class)
class EdrControllerTest {

	@Mock
	private IEdr edrService;
	@Mock
	private IConfigurations configService;

	@InjectMocks
	private EdrController controller;

	private MultiValueMap<String, String> headers() {
		MultiValueMap<String, String> h = new LinkedMultiValueMap<>();
		h.put("realm", Collections.singletonList("test"));
		return h;
	}

	@Test
	void handleNonBrowserSubmissions_retornaOk() {
		ResponseEntity<String> rsp = controller.handleNonBrowserSubmissions(headers());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void loginTicket_ok_retornaOK() throws Exception {
		when(edrService.loginEdenred(any())).thenReturn("TOKEN");
		ResponseEntity<String> rsp = controller.loginTicket(headers());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void loginTicket_emulatorException_retornaConflict() throws Exception {
		when(edrService.loginEdenred(any())).thenThrow(new EmulatorException("err"));
		ResponseEntity<String> rsp = controller.loginTicket(headers());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void loginTicket_confException_retornaStatusDeExcepcion() throws Exception {
		doThrow(new ConfException("x", "500", TypeResponse.HTTP_RESPONSE_500)).when(configService)
				.evaluateEndpoint(anyString());
		ResponseEntity<String> rsp = controller.loginTicket(headers());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rsp.getStatusCode());
	}

	@Test
	void loginJunaeb_ok_retornaOK() throws Exception {
		when(edrService.loginEdenred(any())).thenReturn("TOKEN");
		ResponseEntity<String> rsp = controller.loginJunaeb(headers());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void loginJunaeb_error_retornaConflict() throws Exception {
		when(edrService.loginEdenred(any())).thenThrow(new EmulatorException("err"));
		ResponseEntity<String> rsp = controller.loginJunaeb(headers());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void loginJunaeb_confException_retornaStatusDeExcepcion() throws Exception {
		doThrow(new ConfException("x", "503", TypeResponse.HTTP_RESPONSE_503)).when(configService)
				.evaluateEndpoint(anyString());
		ResponseEntity<String> rsp = controller.loginJunaeb(headers());
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, rsp.getStatusCode());
	}
}
