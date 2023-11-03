package cl.jonnattan.emulator.controllers;

import java.util.UUID;
import java.util.logging.Logger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.jonnattan.emulator.dto.EdrPayAuthorizeRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentLogonRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenEnrollmentRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenLogonRequestDTO;
import cl.jonnattan.emulator.dto.ErrorData;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.interfaces.ICard;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.interfaces.IPrm;
import cl.jonnattan.emulator.interfaces.ITransactions;
import cl.jonnattan.emulator.interfaces.ITrm;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.EmulatorException;
import jakarta.validation.Valid;

/**
 * Controlles principal de emulador
 * 
 * @author Jonnattan Griffiths
 * @since Programa EMULADOR
 * @version 1.0 del 22-06-2020
 * 
 */
@RestController
@RequestMapping("/")
public class CetipaController {
	private static final Logger logger = Logger.getLogger(CetipaController.class.getName());

	@Autowired
	private ITransactions transactionService;

	@Autowired
	private ITrm trmService;

	@Autowired
	private IPrm prmService;

	@Autowired
	private ICard cardService;

	@Autowired
	private IConfigurations configService;

	/**
	 * Autorizar transaccion
	 * 
	 * @param transaction
	 * @param headersRx
	 * @return
	 */
	@PostMapping(value = "/pay/ptm/v1/authorizations")
	public ResponseEntity<IEmulator> authorizations(@Valid @RequestBody EdrPayAuthorizeRequestDTO transaction,
			@RequestHeader HttpHeaders headersRx) {
		logger.info("****************************/pay/ptm/v1/authorizations****************************");

		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/pay/ptm/v1/authorizations");
			response = transactionService.createTransaction(transaction, headersRx);
			headersTx.add("location", "EMULATOR/APITEC/authorizations/EMULATOR-ID-HEADER");
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}
		return new ResponseEntity<>(response, headersTx, status);
	}

	@PostMapping(value = "/pay/ptm/v1/voids")
	public ResponseEntity<IEmulator> reverse(@Valid @RequestBody EdrPaymentReverseRequestDTO request,
			@RequestHeader HttpHeaders headersRx) {
		logger.info("****************************/pay/ptm/v1/voids****************************");
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		// Se pasa el mismo requestid
		String id = headersRx.get("requestid").get(0);
		headersTx.add("requestid", id);
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/pay/ptm/v1/voids");
			response = transactionService.reverseTransaction(request, headersRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}

		return new ResponseEntity<>(response, headersTx, status);
	}

	/**
	 * Enrola una tarjeta en apitec.
	 * 
	 * @param request
	 * @param headersRx
	 * @return
	 */
	@PostMapping(value = "/tsp/ttm/v1/enrollments")
	public ResponseEntity<IEmulator> deviceEnroll(@Valid @RequestBody EdrTokenEnrollmentRequestDTO request,
			@RequestHeader HttpHeaders headersRx) {
		logger.info("****************************/tsp/ttm/v1/enrollments****************************");
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/tsp/ttm/v1/enrollments");
			response = cardService.enrollDevice(request, headersRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}

		return new ResponseEntity<IEmulator>(response, headersTx, status);
	}

	/**
	 * 
	 * @param request
	 * @param headersRx
	 * @return
	 */
	@PostMapping(value = "/tsp/ttm/v1/par/search")
	public ResponseEntity<IEmulator> deviceSearch(@Valid @RequestBody EdrTokenGetDigitalPanRequestDTO request,
			@RequestHeader HttpHeaders headersRx) {
		logger.info("****************************/tsp/ttm/v1/par/search****************************");
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/tsp/ttm/v1/par/search");
			response = cardService.cardSearch(request, headersRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}

		return new ResponseEntity<IEmulator>(response, headersTx, status);
	}

	/**
	 * 
	 * @param request
	 * @param headersRx
	 * @return
	 */
	@GetMapping(value = "/tsp/ttm/v1/tokens/{token}")
	public ResponseEntity<IEmulator> getToken(@PathVariable String token, @RequestHeader HttpHeaders headersRx) {
		logger.info("****************************/tsp/ttm/v1/tokens/{token}****************************");
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/tsp/ttm/v1/tokens/{token}");
			response = cardService.getToken(token, headersRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}
		return new ResponseEntity<IEmulator>(response, headersTx, status);
	}

	/**
	 * 
	 * @param request
	 * @param headersRx
	 * @return
	 */
	@PostMapping(value = "/tsp/ttm/v1/tokens/{token}/acquired")
	public ResponseEntity<String> getAcquire(@PathVariable String token, @RequestHeader HttpHeaders headerRx) {
		logger.info("****************************/tsp/ttm/v1/tokens/{token}/acquired****************************");

		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;

		String requestid = headerRx.get("requestid").get(0);
		String client_id = headerRx.get("client_id").get(0);
		String access_token = headerRx.get("access_token").get(0);

		logger.info("requestid   : " + requestid);
		logger.info("client_id   : " + client_id);
		logger.info("access_token: " + access_token);
		logger.info("token       : " + token);

		return new ResponseEntity<String>("OK", headersTx, status);
	}

	/**
	 * 
	 * @param requestor
	 * @param request
	 * @param header
	 * @return
	 */
	@PostMapping(value = "/pay/prm/v1/requestors/{requestor}/logon")
	public ResponseEntity<IEmulator> logingout(@PathVariable String requestor,
			@RequestBody EdrPaymentLogonRequestDTO request, @RequestHeader HttpHeaders header) {
		logger.info("****************************/pay/prm/v1/requestors/{requestor}/logon****************************");
		IEmulator response = null;
		HttpStatus status = HttpStatus.OK;
		HttpHeaders headersTx = new HttpHeaders();
		try {
			configService.evaluateEndpoint("/pay/prm/v1/requestors/{requestor}/logon");
			response = prmService.logon(requestor, header, request);
			// Este token es muy importante. Apitec lo cambi'o hace poco a un valor m'as
			// grande

			String accessToken = "EMULATOR-PAYMENT";
			for (int i = 0; i < 100; i++)
				accessToken += "-" + UUID.randomUUID().toString();

			headersTx.add("access_token", accessToken);
			logger.info("PRM Response Header[access_token]: " + accessToken);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}
		return new ResponseEntity<IEmulator>(response, headersTx, status);
	}

	/**
	 * 
	 * @param requestor
	 * @param request
	 * @param headersRx
	 * @return
	 */
	@PostMapping(value = "/tsp/trm/v1/requestors/{requestor}/logon")
	public ResponseEntity<IEmulator> logingout(@PathVariable String requestor,
			@RequestBody EdrTokenLogonRequestDTO request, @RequestHeader HttpHeaders headersRx) {
		logger.info("****************************/tsp/trm/v1/requestors/{requestor}/logon****************************");
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/tsp/trm/v1/requestors/{requestor}/logon");
			response = trmService.logon(requestor, headersRx, request);
			
			
			String accessToken = "EMULATOR-TOKEN";
			for (int i = 0; i < 100; i++)
				accessToken += "-" + UUID.randomUUID().toString();

			headersTx.add("access_token", accessToken);
			logger.info("TRM Response Header[access_token]: " + accessToken);
			
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}
		return new ResponseEntity<IEmulator>(response, headersTx, status);
	}

	/**
	 * Obtiene los datos relacionados con el criptograma
	 * 
	 * @param token
	 * @param request
	 * @param headersRx
	 * @return
	 */
	@PostMapping(value = "/tsp/ttm/v1/tokens/{token}/cryptograms")
	public ResponseEntity<IEmulator> cryptograms(@PathVariable String token,
			@RequestBody EdrPaymentCreateCryptogramRequestDTO request, @RequestHeader HttpHeaders headersRx) {
		logger.info("****************************/tsp/ttm/v1/tokens/{token}/cryptograms****************************");
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/tsp/ttm/v1/tokens/{token}/cryptograms");
			response = transactionService.createCriptogram(request, headersRx, token);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		} catch (ConfException e) {
			status = e.getStatus();
			response = new ErrorData();
			((ErrorData) response).setCode(e.getCode());
			((ErrorData) response).setMessage(e.getMessage());
		}
		return new ResponseEntity<IEmulator>(response, headersTx, status);
	}

}
