package cl.jonnattan.emulator.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import cl.jonnattan.emulator.User;
import cl.jonnattan.emulator.daos.IDaoUser;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.dto.user.UserListDTOResponse;
import cl.jonnattan.emulator.dto.user.UserSaveResponse;
import cl.jonnattan.emulator.enums.EUserType;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

	@Mock
	private IDaoUser userRepository;

	@Mock
	private IUtilities util;

	@InjectMocks
	private PageService service;

	private User buildUser() {
		User u = new User();
		u.setId(1L);
		u.setRut("11111111-1");
		u.setMail("a@b.cl");
		u.setNameUser("user");
		u.setFullName("Nombre");
		u.setAge(30);
		u.setAddress("Dir");
		u.setCity("Stgo");
		u.setMobile("9");
		u.setType(EUserType.NORMAL);
		return u;
	}

	@Test
	void getUsers_retornaListaMapeada() throws EmulatorException {
		when(userRepository.findAll()).thenReturn(Arrays.asList(buildUser(), buildUser()));

		IEmulator response = service.getUsers(new HttpHeaders());

		assertInstanceOf(UserListDTOResponse.class, response);
		assertEquals(2, ((UserListDTOResponse) response).getUsers().size());
	}

	@Test
	void getUsers_cuandoFalla_lanzaEmulatorException() {
		when(userRepository.findAll()).thenThrow(new RuntimeException("boom"));
		EmulatorException ex = assertThrows(EmulatorException.class, () -> service.getUsers(new HttpHeaders()));
		assertEquals("6500", ex.getCode());
	}

	@Test
	void save_usuarioNuevo_creaYRetornaId() throws EmulatorException {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("userName", Collections.singletonList("nuevo"));
		params.put("rut", Collections.singletonList("22222222-2"));
		params.put("mail", Collections.singletonList("x@y.cl"));
		params.put("pass", Collections.singletonList("secret"));
		params.put("type", Collections.singletonList("Normal"));
		params.put("age", Collections.singletonList("25"));

		when(userRepository.findByRut(any())).thenReturn(null);
		when(userRepository.findByMail(any())).thenReturn(null);
		when(util.SHA256(any())).thenReturn("HASH");
		User saved = buildUser();
		saved.setId(999L);
		when(userRepository.save(any())).thenReturn(saved);

		IEmulator response = service.save(params, new HttpHeaders());

		assertInstanceOf(UserSaveResponse.class, response);
		assertEquals(999L, ((UserSaveResponse) response).getId());
	}

	@Test
	void save_usuarioExiste_actualiza() throws EmulatorException {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("userName", Collections.singletonList("existe"));
		params.put("rut", Collections.singletonList("11111111-1"));
		params.put("type", Collections.singletonList("Administrador"));

		User u = buildUser();
		when(userRepository.findByRut(any())).thenReturn(u);
		when(userRepository.save(any())).thenReturn(u);

		IEmulator response = service.save(params, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void save_sinNameUser_lanzaEmulatorException() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("rut", Collections.singletonList("X"));

		EmulatorException ex = assertThrows(EmulatorException.class,
				() -> service.save(params, new HttpHeaders()));
		assertEquals("6500", ex.getCode());
	}
}
