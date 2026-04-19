package cl.jonnattan.emulator.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.Card;
import cl.jonnattan.emulator.Device;
import cl.jonnattan.emulator.daos.IDaoCard;
import cl.jonnattan.emulator.daos.IDaoDevice;
import cl.jonnattan.emulator.dto.EdrTokenEnrollResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenEnrollmentRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetTokenResponseDTO;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;
import cl.jonnattan.emulator.utils.UtilConst;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

	@Mock
	private IDaoCard cardRepository;
	@Mock
	private IDaoDevice deviceRepository;
	@Mock
	private IUtilities util;

	@InjectMocks
	private CardService service;

	private EdrTokenGetDigitalPanRequestDTO buildSearchRequest() {
		EdrTokenGetDigitalPanRequestDTO r = new EdrTokenGetDigitalPanRequestDTO();
		EdrTokenGetDigitalPanRequestDTO.RequestorInfo ri = new EdrTokenGetDigitalPanRequestDTO.RequestorInfo();
		ri.setRid("123");
		r.setRequestorInfo(ri);
		EdrTokenGetDigitalPanRequestDTO.CardInfo ci = new EdrTokenGetDigitalPanRequestDTO.CardInfo();
		EdrTokenGetDigitalPanRequestDTO.ProfileData pd = new EdrTokenGetDigitalPanRequestDTO.ProfileData();
		pd.setProfile("CIPHER-PROFILE");
		ci.setData(pd);
		r.setCardInfo(ci);
		return r;
	}

	@Test
	void cardSearch_tarjetaEncontrada_retornaResponse() throws EmulatorException {
		EdrTokenGetDigitalPanRequestDTO req = buildSearchRequest();
		Map<String, Object> map = new HashMap<>();
		map.put("fpan", "4242424242424242");

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptRSA(any())).thenReturn("{}");
		when(util.toMap(any())).thenReturn(map);
		when(util.getTokenCard(any())).thenReturn("TOKEN-X");
		Card card = new Card();
		card.setCardNumber("4242424242424242");
		card.setToken("TOKEN-X");
		when(cardRepository.findByCardNumber("4242424242424242")).thenReturn(card);

		EdrTokenGetDigitalPanResponseDTO response = service.cardSearch(req, new HttpHeaders());

		assertNotNull(response);
		assertNotNull(response.getCardInfo());
	}

	@Test
	void cardSearch_tarjetaNoExiste_laRegistraYResponde() throws EmulatorException {
		EdrTokenGetDigitalPanRequestDTO req = buildSearchRequest();
		Map<String, Object> map = new HashMap<>();
		map.put("fpan", "99");

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptRSA(any())).thenReturn("{}");
		when(util.toMap(any())).thenReturn(map);
		when(util.getTokenCard(any())).thenReturn("TK");
		when(cardRepository.findByCardNumber(any())).thenReturn(null);
		when(cardRepository.save(any())).thenAnswer(inv -> {
			Card c = inv.getArgument(0);
			c.setId(1L);
			return c;
		});

		EdrTokenGetDigitalPanResponseDTO response = service.cardSearch(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void cardSearch_error_lanzaEmulatorException() {
		EdrTokenGetDigitalPanRequestDTO req = buildSearchRequest();
		when(util.toJson(any())).thenThrow(new RuntimeException("boom"));
		assertThrows(EmulatorException.class, () -> service.cardSearch(req, new HttpHeaders()));
	}

	@Test
	void getToken_deviceNoExiste_lanzaEmulatorException() {
		when(deviceRepository.findByToken("X")).thenReturn(null);
		EmulatorException ex = assertThrows(EmulatorException.class,
				() -> service.getToken("X", new HttpHeaders()));
		assertNotNull(ex.getMessage());
	}

	@Test
	void getToken_deviceSinCard_lanzaEmulatorException() {
		Device d = new Device();
		d.setCard("C1");
		when(deviceRepository.findByToken("TK")).thenReturn(d);
		when(cardRepository.findByToken("C1")).thenReturn(null);

		assertThrows(EmulatorException.class, () -> service.getToken("TK", new HttpHeaders()));
	}

	@Test
	void getToken_ok_retornaTokenInfo() throws EmulatorException {
		Device d = new Device();
		d.setCard("C1");
		d.setToken("TKN");
		when(deviceRepository.findByToken("TK")).thenReturn(d);
		Card c = new Card();
		c.setCardNumber("4242");
		when(cardRepository.findByToken("C1")).thenReturn(c);
		when(util.encryptRSA(any())).thenReturn("CIPHER");

		EdrTokenGetTokenResponseDTO response = service.getToken("TK", new HttpHeaders());

		assertNotNull(response);
		assertNotNull(response.getTokeninfo());
	}

	@Test
	void enrollDevice_cardNoIdentificada_lanzaEmulatorException() {
		EdrTokenEnrollmentRequestDTO req = buildEnrollRequest();

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptRSA(any())).thenReturn("{}");
		when(util.toMap(any())).thenReturn(new HashMap<>());
		when(util.getTokenCard(any())).thenReturn("TK");

		EmulatorException ex = assertThrows(EmulatorException.class,
				() -> service.enrollDevice(req, new HttpHeaders()));
		assertNotNull(ex.getMessage());
	}

	@Test
	void enrollDevice_cardYDeviceExisten_retornaExistente() throws EmulatorException {
		EdrTokenEnrollmentRequestDTO req = buildEnrollRequest();
		Map<String, Object> map = new HashMap<>();
		map.put("fpan", "4242");

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptRSA(any())).thenReturn("{}");
		when(util.toMap(any())).thenReturn(map);
		when(util.getTokenCard(any())).thenReturn("TK-CARD");

		Card card = new Card();
		card.setToken("TK-CARD");
		when(cardRepository.findByCardNumber("4242")).thenReturn(card);
		Device d = new Device();
		d.setToken("DEV-TOKEN");
		when(deviceRepository.findByCard("TK-CARD")).thenReturn(d);

		EdrTokenEnrollResponseDTO response = service.enrollDevice(req, new HttpHeaders());

		assertNotNull(response);
		assertNotNull(response.getTokenInfo());
	}

	@Test
	void enrollDevice_cardExisteSinDevice_creaNuevo() throws EmulatorException {
		EdrTokenEnrollmentRequestDTO req = buildEnrollRequest();
		Map<String, Object> map = new HashMap<>();
		map.put("fpan", "4242");

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptRSA(any())).thenReturn("{}");
		when(util.toMap(any())).thenReturn(map);
		when(util.getTokenCard(any())).thenReturn("TK-CARD");
		when(util.SHA256(any())).thenReturn("NEW-DEVICE-TK");

		Card card = new Card();
		card.setToken("TK-CARD");
		when(cardRepository.findByCardNumber("4242")).thenReturn(card);
		when(deviceRepository.findByCard("TK-CARD")).thenReturn(null);

		EdrTokenEnrollResponseDTO response = service.enrollDevice(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void processTNP_tarjetaNueva_guardaYRetorna() throws Exception {
		HttpServletRequest req = mockTnpRequest("/api/v1/foods/cards/ENCODED");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer XYZ");
		headers.add(UtilConst.X_CLIENT_ID, "client");
		headers.add(UtilConst.X_CLIENT_SECRET, "secret");

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptAES(any())).thenReturn("4242");
		when(util.SHA256(any())).thenReturn("SHA");
		when(cardRepository.findByCardNumber("4242")).thenReturn(null);
		when(cardRepository.save(any())).thenAnswer(inv -> {
			Card c = inv.getArgument(0);
			c.setId(10L);
			return c;
		});

		IEmulator response = service.processTNP(req, headers);

		assertNotNull(response);
	}

	private EdrTokenEnrollmentRequestDTO buildEnrollRequest() {
		EdrTokenEnrollmentRequestDTO r = new EdrTokenEnrollmentRequestDTO();
		EdrTokenEnrollmentRequestDTO.CardInfo ci = new EdrTokenEnrollmentRequestDTO.CardInfo();
		EdrTokenEnrollmentRequestDTO.ProfileData pd = new EdrTokenEnrollmentRequestDTO.ProfileData();
		pd.setProfile("CIPHER");
		ci.setData(pd);
		r.setCardInfo(ci);
		return r;
	}

	private HttpServletRequest mockTnpRequest(String uri) throws IOException {
		HttpServletRequest req = org.mockito.Mockito.mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn(uri);
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
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
