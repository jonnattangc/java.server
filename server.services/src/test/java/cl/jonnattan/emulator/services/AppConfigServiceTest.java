package cl.jonnattan.emulator.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.jonnattan.emulator.Configuration;
import cl.jonnattan.emulator.daos.IDaoConfiguration;
import cl.jonnattan.emulator.dto.AppConfigurationRequestDTO;
import cl.jonnattan.emulator.dto.AppListConfigurationDTOResponse;
import cl.jonnattan.emulator.enums.TypeResponse;
import cl.jonnattan.emulator.utils.ConfException;

@ExtendWith(MockitoExtension.class)
class AppConfigServiceTest {

	@Mock
	private IDaoConfiguration configRepository;

	@InjectMocks
	private AppConfigService service;

	private AppConfigurationRequestDTO buildRequest(Boolean error) {
		AppConfigurationRequestDTO dto = new AppConfigurationRequestDTO();
		dto.setEndPoint("/test/endpoint");
		dto.setError(error);
		dto.setCode("500");
		dto.setMessage("Error simulado");
		dto.setType(500);
		return dto;
	}

	@Test
	void updateConfigurations_existenteConError_actualizaYGuarda() throws ConfException {
		Configuration existing = new Configuration();
		existing.setEndpoint("/test/endpoint");
		when(configRepository.findByEndpoint("/test/endpoint")).thenReturn(existing);

		String result = service.updateConfigurations(buildRequest(Boolean.TRUE));

		assertEquals("Se actualiza configuración", result);
		verify(configRepository).save(existing);
		assertEquals("500", existing.getCode());
	}

	@Test
	void updateConfigurations_existenteSinError_limpiaCampos() throws ConfException {
		Configuration existing = new Configuration();
		when(configRepository.findByEndpoint(any())).thenReturn(existing);

		service.updateConfigurations(buildRequest(Boolean.FALSE));

		assertEquals(null, existing.getCode());
		assertEquals(null, existing.getMessage());
		assertEquals(TypeResponse.HTTP_RESPONSE_200, existing.getType());
		verify(configRepository).save(existing);
	}

	@Test
	void updateConfigurations_noExiste_delegaEnCreate() throws ConfException {
		when(configRepository.findByEndpoint(any())).thenReturn(null);

		String result = service.updateConfigurations(buildRequest(Boolean.TRUE));

		assertNotNull(result);
		verify(configRepository).save(any(Configuration.class));
	}

	@Test
	void createConfigurations_conError_guardaConfiguracion() throws ConfException {
		String result = service.createConfigurations(buildRequest(Boolean.TRUE));

		assertEquals("Se crea configuración", result);
		verify(configRepository).save(any(Configuration.class));
	}

	@Test
	void createConfigurations_sinError_guardaConTipoOK() throws ConfException {
		service.createConfigurations(buildRequest(Boolean.FALSE));
		verify(configRepository).save(any(Configuration.class));
	}

	@Test
	void saveConfigurations_existente_llamaUpdate() throws ConfException {
		Configuration existing = new Configuration();
		when(configRepository.findByEndpoint("/test/endpoint")).thenReturn(existing, existing);

		String result = service.saveConfigurations(buildRequest(Boolean.TRUE));

		assertNotNull(result);
		verify(configRepository).save(any(Configuration.class));
	}

	@Test
	void saveConfigurations_noExiste_llamaCreate() throws ConfException {
		when(configRepository.findByEndpoint(any())).thenReturn(null);

		String result = service.saveConfigurations(buildRequest(Boolean.TRUE));

		assertNotNull(result);
		verify(configRepository).save(any(Configuration.class));
	}

	@Test
	void getConfigurations_retornaListaConElementos() throws ConfException {
		Configuration c = new Configuration();
		c.setEndpoint("/e");
		c.setCode("500");
		c.setMessage("m");
		c.setError(Boolean.TRUE);
		c.setType(TypeResponse.HTTP_RESPONSE_500);
		when(configRepository.findAll()).thenReturn(Collections.singletonList(c));

		AppListConfigurationDTOResponse result = service.getConfigurations();

		assertNotNull(result);
		assertEquals(1, result.getConfigurations().size());
	}

	@Test
	void evaluateEndpoint_sinConfiguracion_noLanza() throws ConfException {
		when(configRepository.findByEndpoint(any())).thenReturn(null);
		service.evaluateEndpoint("/cualquiera");
		verify(configRepository).findByEndpoint("/cualquiera");
	}

	@Test
	void evaluateEndpoint_conError_lanzaConfException() {
		Configuration c = new Configuration();
		c.setError(Boolean.TRUE);
		c.setCode("503");
		c.setMessage("Servicio caído");
		c.setType(TypeResponse.HTTP_RESPONSE_503);
		when(configRepository.findByEndpoint(any())).thenReturn(c);

		ConfException ex = assertThrows(ConfException.class, () -> service.evaluateEndpoint("/e"));
		assertEquals("503", ex.getCode());
	}

	@Test
	void evaluateEndpoint_sinError_noLanza() throws ConfException {
		Configuration c = new Configuration();
		c.setError(Boolean.FALSE);
		when(configRepository.findByEndpoint(any())).thenReturn(c);

		service.evaluateEndpoint("/e");
		verify(configRepository, never()).save(any());
	}
}
