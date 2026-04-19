package cl.jonnattan.emulator.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import cl.jonnattan.emulator.dto.ErrorData;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.dto.user.UserListDTOResponse;
import cl.jonnattan.emulator.enums.TypeResponse;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.interfaces.IPage;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.EmulatorException;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class PageControllerTest {

	@Mock
	private IPage pageService;
	@Mock
	private IConfigurations configService;

	@InjectMocks
	private PageController controller;

	@Test
	void save_ok_retornaOK() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/page/users/save");
		when(pageService.save(any(), any())).thenReturn(new UserListDTOResponse());

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		ResponseEntity<IEmulator> rsp = controller.save(params, req, new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void save_emulatorException_retornaConflict() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/page/users/save");
		when(pageService.save(any(), any())).thenThrow(new EmulatorException("x", "500"));

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		ResponseEntity<IEmulator> rsp = controller.save(params, req, new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void save_confException_retornaStatusDeExcepcion() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/page/users/save");
		doThrow(new ConfException("x", "400", TypeResponse.HTTP_RESPONSE_400)).when(configService)
				.evaluateEndpoint(anyString());

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		ResponseEntity<IEmulator> rsp = controller.save(params, req, new HttpHeaders());
		assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
	}

	@Test
	void getUsers_ok_retornaOK() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/page/users");
		when(pageService.getUsers(any())).thenReturn(new UserListDTOResponse());

		ResponseEntity<IEmulator> rsp = controller.getUsers(req, new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void getUsers_error_retornaConflict() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/page/users");
		when(pageService.getUsers(any())).thenThrow(new EmulatorException("x"));

		ResponseEntity<IEmulator> rsp = controller.getUsers(req, new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void handleException_emulatorException_retornaConflictConErrorData() {
		ResponseEntity<Object> rsp = controller.handleException(new EmulatorException("e", "500"), null);
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
		assertEquals(ErrorData.class, rsp.getBody().getClass());
	}

	@Test
	void handleException_excepcionGenerica_retornaConflict() {
		ResponseEntity<Object> rsp = controller.handleException(new RuntimeException("x"), null);
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}
}
