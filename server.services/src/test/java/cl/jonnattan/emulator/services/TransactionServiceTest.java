package cl.jonnattan.emulator.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.Card;
import cl.jonnattan.emulator.Device;
import cl.jonnattan.emulator.Transaction;
import cl.jonnattan.emulator.daos.IDaoCard;
import cl.jonnattan.emulator.daos.IDaoDevice;
import cl.jonnattan.emulator.daos.IDaoTransaction;
import cl.jonnattan.emulator.dto.Authorization;
import cl.jonnattan.emulator.dto.EdrPayAuthorizeRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentAuthorizeResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseResponseDTO;
import cl.jonnattan.emulator.dto.TransactionVoid;
import cl.jonnattan.emulator.enums.TransactionStatus;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;
import cl.jonnattan.emulator.utils.UtilConst;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private IDaoTransaction transactionRepository;
	@Mock
	private IDaoCard cardRepository;
	@Mock
	private IDaoDevice deviceRepository;
	@Mock
	private IUtilities util;

	@InjectMocks
	private TransactionService service;

	private static final ObjectMapper mapper = new ObjectMapper();

	private EdrPayAuthorizeRequestDTO buildAuthRequest() throws Exception {
		String json = "{\"token\":{\"data\":\"CIPHER-DATA\"},\"transaction\":{\"amount\":\"1000\"}}";
		return mapper.readValue(json, EdrPayAuthorizeRequestDTO.class);
	}

	private EdrPaymentReverseRequestDTO buildReverseRequest(String authId) {
		EdrPaymentReverseRequestDTO req = new EdrPaymentReverseRequestDTO();
		TransactionVoid tv = new TransactionVoid();
		Authorization auth = new Authorization();
		auth.setId(authId);
		tv.setAuthorization(auth);
		req.setTransactionVoid(tv);
		return req;
	}

	@Test
	void createTransaction_conDispositivoYTarjeta_responde() throws Exception {
		EdrPayAuthorizeRequestDTO req = buildAuthRequest();
		Map<String, Object> map = new HashMap<>();
		map.put("cryptogram", UtilConst.CRYPTO_PREF + "XYZ");

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptRSA("CIPHER-DATA")).thenReturn("{\"cryptogram\":\"CRIPTOGRAM-EMULATOR-XYZ\"}");
		when(util.toMap(any())).thenReturn(map);
		when(util.cksumSHA256(any())).thenReturn(12345L);
		Device device = new Device();
		device.setCard("TOKEN-CARD");
		when(deviceRepository.findByCryptogram(any())).thenReturn(device);
		Card card = new Card();
		card.setCardNumber("4242424242424242");
		card.setId(1L);
		card.setAmount(100000L);
		when(cardRepository.findByToken("TOKEN-CARD")).thenReturn(card);
		when(cardRepository.findByCardNumber("4242424242424242")).thenReturn(card);

		EdrPaymentAuthorizeResponseDTO response = service.createTransaction(req, new HttpHeaders());

		assertNotNull(response);
		assertEquals(UtilConst.STATUS_APPROVED, response.getTransactionex().getStatus());
		assertEquals(UtilConst.RESPONSE_CODE_200, response.getTransactionex().getResponsecode());
	}

	@Test
	void createTransaction_sinDispositivo_usaDefaultCard() throws Exception {
		EdrPayAuthorizeRequestDTO req = buildAuthRequest();
		Map<String, Object> map = new HashMap<>();
		map.put("cryptogram", UtilConst.CRYPTO_PREF + "N/A");

		when(util.toJson(any())).thenReturn("{}");
		when(util.decryptRSA(any())).thenReturn("{}");
		when(util.toMap(any())).thenReturn(map);
		when(util.cksumSHA256(any())).thenReturn(999L);
		when(deviceRepository.findByCryptogram(any())).thenReturn(null);

		EdrPaymentAuthorizeResponseDTO response = service.createTransaction(req, new HttpHeaders());

		assertNotNull(response);
	}

	@Test
	void createTransaction_cuandoFalla_lanzaEmulatorException() throws Exception {
		EdrPayAuthorizeRequestDTO req = buildAuthRequest();
		when(util.toJson(any())).thenThrow(new RuntimeException("boom"));

		EmulatorException ex = assertThrows(EmulatorException.class,
				() -> service.createTransaction(req, new HttpHeaders()));
		assertEquals("5544", ex.getCode());
	}

	@Test
	void reverseTransaction_transaccionAutorizada_devuelveAPROBADO() throws EmulatorException {
		EdrPaymentReverseRequestDTO req = buildReverseRequest("AUTH-1");

		Transaction tx = new Transaction();
		tx.setId(1L);
		tx.setStatus(TransactionStatus.AUTHORIZED);
		tx.setCard("4242");
		tx.setAmount("500");

		Card card = new Card();
		card.setId(1L);
		card.setAmount(1000L);

		when(util.toJson(any())).thenReturn("{}");
		when(transactionRepository.findByAuthorizationId("AUTH-1")).thenReturn(tx);
		when(cardRepository.findByCardNumber("4242")).thenReturn(card);

		EdrPaymentReverseResponseDTO response = service.reverseTransaction(req, new HttpHeaders());

		assertEquals(UtilConst.STATUS_APPROVED, response.getTransactionex().getStatus());
		assertEquals(UtilConst.EMULATOR_MCC, response.getTransactionex().getMcc());
	}

	@Test
	void reverseTransaction_transaccionNoExiste_lanzaEmulatorException() {
		EdrPaymentReverseRequestDTO req = buildReverseRequest("NOEXISTE");

		when(util.toJson(any())).thenReturn("{}");
		when(transactionRepository.findByAuthorizationId("NOEXISTE")).thenReturn(null);

		assertThrows(EmulatorException.class, () -> service.reverseTransaction(req, new HttpHeaders()));
	}

	@Test
	void reverseTransaction_estadoInvalido_lanzaEmulatorException() {
		EdrPaymentReverseRequestDTO req = buildReverseRequest("X");

		Transaction tx = new Transaction();
		tx.setStatus(TransactionStatus.REVERSED);

		when(util.toJson(any())).thenReturn("{}");
		when(transactionRepository.findByAuthorizationId("X")).thenReturn(tx);

		assertThrows(EmulatorException.class, () -> service.reverseTransaction(req, new HttpHeaders()));
	}

	@Test
	void createCriptogram_conDevice_retornaCriptograma() throws EmulatorException {
		EdrPaymentCreateCryptogramRequestDTO req = new EdrPaymentCreateCryptogramRequestDTO();
		when(util.toJson(any())).thenReturn("{}");
		when(util.SHA256(any())).thenReturn("HASH");
		Device device = new Device();
		device.setId(99L);
		when(deviceRepository.findByToken("TKN")).thenReturn(device);

		EdrPaymentCreateCryptogramResponseDTO response = service.createCriptogram(req, new HttpHeaders(), "TKN");

		assertNotNull(response);
		assertEquals(UtilConst.ATC_EMULATOR, response.getTokenInfo().getData().getAtc());
	}

	@Test
	void createCriptogram_sinDevice_retornaIgual() throws EmulatorException {
		EdrPaymentCreateCryptogramRequestDTO req = new EdrPaymentCreateCryptogramRequestDTO();
		when(util.toJson(any())).thenReturn("{}");
		when(util.SHA256(any())).thenReturn("HASH");
		when(deviceRepository.findByToken("TKN")).thenReturn(null);

		EdrPaymentCreateCryptogramResponseDTO response = service.createCriptogram(req, new HttpHeaders(), "TKN");

		assertNotNull(response);
	}

	@Test
	void createCriptogram_cuandoFalla_lanzaEmulatorException() {
		EdrPaymentCreateCryptogramRequestDTO req = new EdrPaymentCreateCryptogramRequestDTO();
		when(util.toJson(any())).thenThrow(new RuntimeException("boom"));

		EmulatorException ex = assertThrows(EmulatorException.class,
				() -> service.createCriptogram(req, new HttpHeaders(), "TKN"));
		assertEquals("5423", ex.getCode());
	}
}
