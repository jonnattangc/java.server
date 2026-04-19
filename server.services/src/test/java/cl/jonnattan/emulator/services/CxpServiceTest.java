package cl.jonnattan.emulator.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import cl.jonnattan.emulator.dto.cxp.CxpResponseDTO;
import cl.jonnattan.emulator.dto.cxp.ICxpResponse;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class CxpServiceTest {

	@Mock
	private RestTemplate restTemplateWithTimeout;
	@Mock
	private IUtilities util;

	@InjectMocks
	private CxpService service;

	@BeforeEach
	void init() {
		ReflectionTestUtils.setField(service, "address", "http://a,http://b,http://c");
		ReflectionTestUtils.setField(service, "keysOt", "k1,k2,k3");
		ReflectionTestUtils.setField(service, "keysGeo", "g1,g2,g3");
		ReflectionTestUtils.setField(service, "keysCotizacion", "c1,c2,c3");
		ReflectionTestUtils.setField(service, "index", 2);
		service.init();
	}

	@Test
	void processPostRequest_rating_usaCotKey() throws Exception {
		HttpServletRequest req = mockRequest("/cxp/rating/api/v1.0/rates/courier");
		CxpResponseDTO body = new CxpResponseDTO();
		ResponseEntity<CxpResponseDTO> rsp = ResponseEntity.ok(body);
		when(restTemplateWithTimeout.postForEntity(any(String.class), any(HttpEntity.class), eq(CxpResponseDTO.class)))
				.thenReturn(rsp);
		when(util.toJson(any())).thenReturn("{}");

		ICxpResponse response = service.processPostRequest(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void processPostRequest_transportOrders_usaOtKey() throws Exception {
		HttpServletRequest req = mockRequest("/cxp/transport-orders/api/v1.0/transport-orders");
		CxpResponseDTO body = new CxpResponseDTO();
		when(restTemplateWithTimeout.postForEntity(any(String.class), any(HttpEntity.class), eq(CxpResponseDTO.class)))
				.thenReturn(ResponseEntity.ok(body));
		when(util.toJson(any())).thenReturn("{}");

		ICxpResponse response = service.processPostRequest(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void processPostRequest_errorRest_lanzaEmulatorException() throws Exception {
		HttpServletRequest req = mockRequest("/cxp/otro");
		when(util.toJson(any())).thenReturn("{}");
		when(restTemplateWithTimeout.postForEntity(any(String.class), any(HttpEntity.class), eq(CxpResponseDTO.class)))
				.thenThrow(new RuntimeException("boom"));

		assertThrows(EmulatorException.class, () -> service.processPostRequest(req, new HttpHeaders()));
	}

	@Test
	void processGetRequest_dev_cambiaAmbiente() throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/cxp/dev");

		String response = service.processGetRequest(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void processGetRequest_qa_cambiaAmbiente() throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/cxp/qa");

		String response = service.processGetRequest(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void processGetRequest_prodProduction_cambiaAmbiente() throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/cxp/prod/production");

		String response = service.processGetRequest(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void processGetRequest_uriInvalida_lanzaEmulatorException() {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/cxp/otro");

		assertThrows(EmulatorException.class, () -> service.processGetRequest(req, new HttpHeaders()));
	}

	private HttpServletRequest mockRequest(String uri) throws IOException {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn(uri);
		ByteArrayInputStream bais = new ByteArrayInputStream("{\"body\":1}".getBytes());
		when(req.getInputStream()).thenReturn(new ServletInputStream() {
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
		});
		return req;
	}
}
