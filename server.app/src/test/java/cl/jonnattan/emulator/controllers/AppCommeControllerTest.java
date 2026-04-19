package cl.jonnattan.emulator.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import cl.jonnattan.emulator.dto.CommerceMailResponseDTO;
import cl.jonnattan.emulator.dto.CommerceTokenResponseDTO;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

class AppCommeControllerTest {

	private final AppCommeController controller = new AppCommeController();

	@Test
	void sendMail_conBody_retornaOK() throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		ByteArrayInputStream bais = new ByteArrayInputStream("{\"a\":1}".getBytes());
		when(req.getInputStream()).thenReturn(stream(bais));

		ResponseEntity<CommerceMailResponseDTO> rsp = controller.sendMail(req, new LinkedMultiValueMap<>());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void sendMail_ioError_retornaOK() throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getInputStream()).thenThrow(new IOException("boom"));

		ResponseEntity<CommerceMailResponseDTO> rsp = controller.sendMail(req, new LinkedMultiValueMap<>());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void getToken_retornaTokenConAccessToken() {
		ResponseEntity<CommerceTokenResponseDTO> rsp = controller.getToken(new LinkedMultiValueMap<>());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
		assertNotNull(rsp.getBody().getAccess_token());
	}

	private ServletInputStream stream(ByteArrayInputStream bais) {
		return new ServletInputStream() {
			@Override
			public int read() {
				return bais.read();
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener listener) {
				// noop
			}
		};
	}
}
