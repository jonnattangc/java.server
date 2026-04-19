package cl.jonnattan.emulator.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.jonnattan.emulator.dto.AppConfigurationRequestDTO;
import cl.jonnattan.emulator.dto.AppListConfigurationDTOResponse;
import cl.jonnattan.emulator.enums.TypeResponse;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.utils.ConfException;

@ExtendWith(MockitoExtension.class)
class AppControllerTest {

	@Mock
	private IConfigurations configService;

	@InjectMocks
	private AppController controller;

	private AppConfigurationRequestDTO req() {
		AppConfigurationRequestDTO dto = new AppConfigurationRequestDTO();
		dto.setEndPoint("/x");
		dto.setError(Boolean.TRUE);
		dto.setCode("500");
		dto.setMessage("m");
		dto.setType(500);
		return dto;
	}

	@Test
	void update_ok_retornaOK() throws ConfException {
		when(configService.updateConfigurations(any())).thenReturn("ok");
		ResponseEntity<String> rsp = controller.update(req());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void update_excepcion_retornaStatusDeExcepcion() throws ConfException {
		when(configService.updateConfigurations(any()))
				.thenThrow(new ConfException("err", "500", TypeResponse.HTTP_RESPONSE_500));
		ResponseEntity<String> rsp = controller.update(req());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rsp.getStatusCode());
	}

	@Test
	void create_ok_retornaOK() throws ConfException {
		when(configService.createConfigurations(any())).thenReturn("ok");
		ResponseEntity<String> rsp = controller.create(req());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void create_error_retornaStatusDeExcepcion() throws ConfException {
		when(configService.createConfigurations(any()))
				.thenThrow(new ConfException("x", "400", TypeResponse.HTTP_RESPONSE_400));
		ResponseEntity<String> rsp = controller.create(req());
		assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
	}

	@Test
	void save_ok_retornaOK() throws ConfException {
		when(configService.saveConfigurations(any())).thenReturn("ok");
		ResponseEntity<String> rsp = controller.save(req());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void save_error_retornaStatusDeExcepcion() throws ConfException {
		when(configService.saveConfigurations(any()))
				.thenThrow(new ConfException("x", "409", TypeResponse.HTTP_RESPONSE_409));
		ResponseEntity<String> rsp = controller.save(req());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void list_ok_retornaOK() throws ConfException {
		when(configService.getConfigurations()).thenReturn(new AppListConfigurationDTOResponse());
		ResponseEntity<AppListConfigurationDTOResponse> rsp = controller.list();
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
		assertNotNull(rsp.getBody());
	}

	@Test
	void list_error_retornaStatusDeExcepcion() throws ConfException {
		when(configService.getConfigurations())
				.thenThrow(new ConfException("x", "503", TypeResponse.HTTP_RESPONSE_503));
		ResponseEntity<AppListConfigurationDTOResponse> rsp = controller.list();
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, rsp.getStatusCode());
	}
}
