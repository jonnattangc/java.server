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

import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.dto.user.UserListDTOResponse;
import cl.jonnattan.emulator.enums.TypeResponse;
import cl.jonnattan.emulator.interfaces.ICard;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.EmulatorException;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class EdrEgdControllerTest {

	@Mock
	private ICard cardService;
	@Mock
	private IConfigurations configService;

	@InjectMocks
	private EdrEgdController controller;

	@Test
	void edgCardSearch_ok_retornaOK() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(cardService.processTNP(any(), any())).thenReturn(new UserListDTOResponse());

		ResponseEntity<IEmulator> rsp = controller.edgCardSearch(req, new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void edgCardSearch_emulatorException_retornaConflict() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(cardService.processTNP(any(), any())).thenThrow(new EmulatorException("e", "5000"));

		ResponseEntity<IEmulator> rsp = controller.edgCardSearch(req, new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void edgCardSearch_confException_retornaStatusDeExcepcion() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		doThrow(new ConfException("e", "500", TypeResponse.HTTP_RESPONSE_500)).when(configService)
				.evaluateEndpoint(anyString());

		ResponseEntity<IEmulator> rsp = controller.edgCardSearch(req, new HttpHeaders());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rsp.getStatusCode());
	}
}
