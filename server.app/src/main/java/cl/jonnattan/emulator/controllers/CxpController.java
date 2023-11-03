package cl.jonnattan.emulator.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.jonnattan.emulator.dto.cxp.CxpResponseDTO;
import cl.jonnattan.emulator.dto.cxp.ICxpResponse;
import cl.jonnattan.emulator.interfaces.ICxp;
import cl.jonnattan.emulator.utils.EmulatorException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlles de conf de emulador
 * 
 * @author Jonnattan Griffiths
 * @since Programa EMULADOR 
 * @version 1.0 del 11-05-2021
 * 
 */
@RestController
@RequestMapping("/cxp")
public class CxpController {
	private static final Logger logger = Logger.getLogger(CxpController.class.getName());
	@Autowired
	private ICxp cxpService;

	@PostMapping(value = "/**")
	public ResponseEntity<ICxpResponse> postCxp(HttpServletRequest request, @RequestHeader HttpHeaders headerRx) {
		logger.info("Rcv POST Msg from CXP");
		HttpStatus status = HttpStatus.OK;
		ICxpResponse response = null;
		try {
			response = cxpService.processPostRequest(request, headerRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = new CxpResponseDTO();
			((CxpResponseDTO)response).setStatusCode( -1 );
			((CxpResponseDTO)response).setStatusDescription( e.getMessage() );
			((CxpResponseDTO)response).setErrors( null );
			((CxpResponseDTO)response).setData( null );
		}

		return new ResponseEntity<>(response, status);
	}

	@GetMapping(value = "/**")
	public ResponseEntity<String> getCxp(HttpServletRequest request, @RequestHeader HttpHeaders headerRx) {
		logger.info("Rcv GET msg from CXP");
		HttpStatus status = HttpStatus.OK;
		String response = "Ok";
		try {
			response = cxpService.processGetRequest(request, headerRx);
		} catch (EmulatorException e) {
			status = HttpStatus.CONFLICT;
			response = e.getMessage();
		}
		return new ResponseEntity<>(response, status);
	}
}
