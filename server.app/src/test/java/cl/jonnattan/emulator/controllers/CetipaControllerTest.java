package cl.jonnattan.emulator.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.jonnattan.emulator.dto.EdrPayAuthorizeRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentAuthorizeResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentLogonRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentLogonResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenEnrollResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenEnrollmentRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetTokenResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenLogonRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenLogonResponseDTO;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.enums.TypeResponse;
import cl.jonnattan.emulator.interfaces.ICard;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.interfaces.IPrm;
import cl.jonnattan.emulator.interfaces.ITransactions;
import cl.jonnattan.emulator.interfaces.ITrm;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.EmulatorException;
import cl.jonnattan.emulator.utils.UtilConst;

@ExtendWith(MockitoExtension.class)
class CetipaControllerTest {

	@Mock
	private ITransactions transactionService;
	@Mock
	private ITrm trmService;
	@Mock
	private IPrm prmService;
	@Mock
	private ICard cardService;
	@Mock
	private IConfigurations configService;

	@InjectMocks
	private CetipaController controller;

	private HttpHeaders headersConRequestId() {
		HttpHeaders h = new HttpHeaders();
		h.put(UtilConst.REQUEST_ID, Collections.singletonList("RX-1"));
		h.put(UtilConst.CLIENT_ID, Collections.singletonList("CLT"));
		h.put(UtilConst.ACCESS_TOKEN, Collections.singletonList("TKN"));
		return h;
	}

	@Test
	void authorizations_ok_retornaOK() throws Exception {
		when(transactionService.createTransaction(any(), any())).thenReturn(new EdrPaymentAuthorizeResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.authorizations(new EdrPayAuthorizeRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void authorizations_emulatorException_retornaConflict() throws Exception {
		when(transactionService.createTransaction(any(), any())).thenThrow(new EmulatorException("e", "500"));
		ResponseEntity<IEmulator> rsp = controller.authorizations(new EdrPayAuthorizeRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void authorizations_confException_retornaStatusDeExcepcion() throws Exception {
		doThrow(new ConfException("e", "500", TypeResponse.HTTP_RESPONSE_500)).when(configService)
				.evaluateEndpoint(anyString());
		ResponseEntity<IEmulator> rsp = controller.authorizations(new EdrPayAuthorizeRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rsp.getStatusCode());
	}

	@Test
	void reverse_ok_retornaOK() throws Exception {
		when(transactionService.reverseTransaction(any(), any())).thenReturn(new EdrPaymentReverseResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.reverse(new EdrPaymentReverseRequestDTO(), headersConRequestId());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void reverse_emulatorException_retornaConflict() throws Exception {
		when(transactionService.reverseTransaction(any(), any())).thenThrow(new EmulatorException("e", "500"));
		ResponseEntity<IEmulator> rsp = controller.reverse(new EdrPaymentReverseRequestDTO(), headersConRequestId());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void deviceEnroll_ok_retornaOK() throws Exception {
		when(cardService.enrollDevice(any(), any())).thenReturn(new EdrTokenEnrollResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.deviceEnroll(new EdrTokenEnrollmentRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void deviceEnroll_error_retornaConflict() throws Exception {
		when(cardService.enrollDevice(any(), any())).thenThrow(new EmulatorException("x"));
		ResponseEntity<IEmulator> rsp = controller.deviceEnroll(new EdrTokenEnrollmentRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void deviceSearch_ok_retornaOK() throws Exception {
		when(cardService.cardSearch(any(), any())).thenReturn(new EdrTokenGetDigitalPanResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.deviceSearch(new EdrTokenGetDigitalPanRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void deviceSearch_error_retornaStatusDeExcepcion() throws Exception {
		doThrow(new ConfException("x", "409", TypeResponse.HTTP_RESPONSE_409)).when(configService)
				.evaluateEndpoint(anyString());
		ResponseEntity<IEmulator> rsp = controller.deviceSearch(new EdrTokenGetDigitalPanRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void getToken_ok_retornaOK() throws Exception {
		when(cardService.getToken(any(), any())).thenReturn(new EdrTokenGetTokenResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.getToken("TKN", new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void getToken_error_retornaConflict() throws Exception {
		when(cardService.getToken(any(), any())).thenThrow(new EmulatorException("e"));
		ResponseEntity<IEmulator> rsp = controller.getToken("TKN", new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void getAcquire_ok_retornaOK() {
		ResponseEntity<String> rsp = controller.getAcquire("TKN", headersConRequestId());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
		assertNotNull(rsp.getBody());
	}

	@Test
	void logingoutPrm_ok_retornaOK() throws Exception {
		when(prmService.logon(any(), any(), any())).thenReturn(new EdrPaymentLogonResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.logingout("R1", new EdrPaymentLogonRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void logingoutPrm_error_retornaConflict() throws Exception {
		when(prmService.logon(any(), any(), any())).thenThrow(new EmulatorException("x"));
		ResponseEntity<IEmulator> rsp = controller.logingout("R1", new EdrPaymentLogonRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void logingoutTrm_ok_retornaOK() throws Exception {
		when(trmService.logon(any(), any(), any())).thenReturn(new EdrTokenLogonResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.logingout("R1", new EdrTokenLogonRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void logingoutTrm_error_retornaConflict() throws Exception {
		when(trmService.logon(any(), any(), any())).thenThrow(new EmulatorException("x"));
		ResponseEntity<IEmulator> rsp = controller.logingout("R1", new EdrTokenLogonRequestDTO(), new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}

	@Test
	void cryptograms_ok_retornaOK() throws Exception {
		when(transactionService.createCriptogram(any(), any(), any()))
				.thenReturn(new EdrPaymentCreateCryptogramResponseDTO());
		ResponseEntity<IEmulator> rsp = controller.cryptograms("TKN", new EdrPaymentCreateCryptogramRequestDTO(),
				new HttpHeaders());
		assertEquals(HttpStatus.OK, rsp.getStatusCode());
	}

	@Test
	void cryptograms_error_retornaConflict() throws Exception {
		when(transactionService.createCriptogram(any(), any(), any())).thenThrow(new EmulatorException("e"));
		ResponseEntity<IEmulator> rsp = controller.cryptograms("TKN", new EdrPaymentCreateCryptogramRequestDTO(),
				new HttpHeaders());
		assertEquals(HttpStatus.CONFLICT, rsp.getStatusCode());
	}
}
