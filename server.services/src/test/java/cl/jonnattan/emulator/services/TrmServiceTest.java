package cl.jonnattan.emulator.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.dto.EdrTokenLogonRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenLogonResponseDTO;
import cl.jonnattan.emulator.utils.EmulatorException;

@ExtendWith(MockitoExtension.class)
class TrmServiceTest {

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private TrmService service;

	@Test
	void logon_ok_retornaResponseDTO() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		EdrTokenLogonResponseDTO response = service.logon("REQ", new HttpHeaders(), new EdrTokenLogonRequestDTO());
		assertNotNull(response);
	}

	@Test
	void logon_jsonError_lanzaEmulatorException() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {
			private static final long serialVersionUID = 1L;
		});
		assertThrows(EmulatorException.class,
				() -> service.logon("REQ", new HttpHeaders(), new EdrTokenLogonRequestDTO()));
	}
}
