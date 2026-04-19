package cl.jonnattan.emulator.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import cl.jonnattan.emulator.User;
import cl.jonnattan.emulator.daos.IDaoUser;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;

@ExtendWith(MockitoExtension.class)
class EdrServiceTest {

	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private IDaoUser userRepository;
	@Mock
	private IUtilities util;
	@Mock
	private RestTemplate restTemplateWithTimeout;

	@InjectMocks
	private EdrService service;

	private MultiValueMap<String, String> buildHeaders(String realm) {
		MultiValueMap<String, String> h = new LinkedMultiValueMap<>();
		h.put("wrap_name", Collections.singletonList("john"));
		h.put("wrap_password", Collections.singletonList("pwd"));
		h.put("realm", Collections.singletonList(realm));
		return h;
	}

	@Test
	void loginEdenred_usuarioExistente_actualizaToken() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(util.SHA256(any())).thenReturn("HASH-TOKEN");
		when(util.toJson(any())).thenReturn("{}");
		ResponseEntity<String> rsp = ResponseEntity.ok("no_token_here");
		when(restTemplateWithTimeout.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
				.thenReturn(rsp);

		User existing = new User();
		existing.setId(1L);
		when(userRepository.findByNameUserAndPassword("john", "pwd")).thenReturn(existing);

		String result = service.loginEdenred(buildHeaders("EdenredPrivateWebPortal"));

		assertNotNull(result);
	}

	@Test
	void loginEdenred_usuarioNoExiste_creaUsuario() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(util.SHA256(any())).thenReturn("HASH");
		when(util.toJson(any())).thenReturn("{}");
		when(restTemplateWithTimeout.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
				.thenReturn(ResponseEntity.ok(null));
		when(userRepository.findByNameUserAndPassword(any(), any())).thenReturn(null);

		String result = service.loginEdenred(buildHeaders("EdenredJunaebWebPortal"));

		assertNotNull(result);
	}

	@Test
	void loginEdenred_errorEnProceso_lanzaEmulatorException() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("boom"));
		EmulatorException ex = assertThrows(EmulatorException.class,
				() -> service.loginEdenred(buildHeaders("x")));
		assertNotNull(ex.getMessage());
	}
}
