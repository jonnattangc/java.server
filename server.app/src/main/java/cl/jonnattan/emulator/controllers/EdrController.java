package cl.jonnattan.emulator.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.interfaces.IEdr;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.EmulatorException;

/**
 * Controlles principal de emulador
 * 
 * @author Jonnattan Griffiths
 * @since Programa EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
@RestController
@RequestMapping("/edr")
public class EdrController {
	private static final Logger logger = Logger.getLogger(EdrController.class.getName());

	@Autowired
	private IEdr edrService;

	@Autowired
	private IConfigurations configService;

	@PostMapping(path = "/login/tickettest", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, produces = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseEntity<String> handleNonBrowserSubmissions(@RequestHeader MultiValueMap<String, String> headersRx) {
		logger.info("****************************/login/ticket****************************");
		logger.info("REALM: " + headersRx.get("realm"));
		return new ResponseEntity<>("Thank you for submitting feedback", HttpStatus.OK);
	}

	@PostMapping(value = "/login/ticket", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseEntity<String> loginTicket(@RequestHeader MultiValueMap<String, String> headersRx) {
		logger.info("****************************/login/ticket****************************");

		String response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/login/ticket");
			response = edrService.loginEdenred(headersRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = e.getMessage();
		} catch (ConfException e) {
			status = e.getStatus();
			response = e.getMessage();
		}
		return new ResponseEntity<>(response, headersTx, status);
	}

	@PostMapping(value = "/login/beanuj", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, produces = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseEntity<String> loginJunaeb(@RequestHeader MultiValueMap<String, String> headersRx) {
		logger.info("****************************/login/beanuj****************************");

		String response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/login/beanuj");
			response = edrService.loginEdenred(headersRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = e.getMessage();
		} catch (ConfException e) {
			status = e.getStatus();
			response = e.getMessage();
		}
		return new ResponseEntity<>(response, headersTx, status);
	}

}
