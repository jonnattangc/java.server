package cl.jonnattan.emulator.controllers;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.jonnattan.emulator.dto.ErrorData;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.interfaces.ICard;
import cl.jonnattan.emulator.interfaces.IConfigurations;
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
@RequestMapping("/")
public class EdrEgdController {
	private final static Logger logger = Logger.getLogger(EdrEgdController.class.getName());

	@Autowired
	private ICard cardService;

	@Autowired
	private IConfigurations configService;

	@RequestMapping(value = "/api/v1/foods/cards/**")
	public ResponseEntity<IEmulator> edgCardSearch(HttpServletRequest request, @RequestHeader HttpHeaders headerRx) {
		logger.info("****************************/api/v1/foods/cards******************************");

		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		try {
			configService.evaluateEndpoint("/api/v1/foods/cards");
			response = cardService.processTNP(request, headerRx);
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
