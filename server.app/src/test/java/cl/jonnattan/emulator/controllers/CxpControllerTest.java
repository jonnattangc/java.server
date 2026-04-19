package cl.jonnattan.emulator.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.jonnattan.emulator.dto.cxp.CxpResponseDTO;
import cl.jonnattan.emulator.dto.cxp.ICxpResponse;
import cl.jonnattan.emulator.interfaces.ICxp;
import cl.jonnattan.emulator.utils.EmulatorException;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class CxpControllerTest {

	@Mock
	private ICxp cxpService;

	@InjectMocks
	private CxpController controller;

	@Test
	void postCxp_ok_retornaOK() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(cxpService.processPostRequest(any(), any())).thenReturn(new CxpResponseDTO());

		ResponseEntity<ICxpResponse> rsp = controller.postCxp(req, new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void postCxp_error_retornaConflict() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(cxpService.processPostRequest(any(), any())).thenThrow(new EmulatorException("err"));

		ResponseEntity<ICxpResponse> rsp = controller.postCxp(req, new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void getCxp_ok_retornaOK() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(cxpService.processGetRequest(any(), any())).thenReturn("ok");

		ResponseEntity<String> rsp = controller.getCxp(req, new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void getCxp_error_retornaConflict() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(cxpService.processGetRequest(any(), any())).thenThrow(new EmulatorException("err"));

		ResponseEntity<String> rsp = controller.getCxp(req, new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}
}
