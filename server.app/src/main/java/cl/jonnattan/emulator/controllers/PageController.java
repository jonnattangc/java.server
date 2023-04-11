package cl.jonnattan.emulator.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import cl.jonnattan.emulator.dto.ErrorData;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.interfaces.IPage;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.EmulatorException;

/**
 * Controller para solicitudes desde la web personal
 * 
 * @author Jonnattan Griffiths
 * @since Programa EMULADOR
 * @version 1.0 del 20-02-2023
 * 
 */
@RestController
@RequestMapping("/page")
public class PageController {

	private static final Logger logger = LoggerFactory.getLogger(PageController.class);

	@Autowired
	private IPage pageService;

	@Autowired
	private IConfigurations configService;
	@CrossOrigin(origins = "*")
	@PostMapping(path = "/users/save", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, produces = {
			MediaType.TEXT_HTML_VALUE })

	public ResponseEntity<IEmulator> save(@Valid @RequestParam MultiValueMap<String, String> params,
			HttpServletRequest request, @RequestHeader HttpHeaders headers) {
		logger.info("---------------------------------------------------------------------------------------");
		HttpStatus status = HttpStatus.OK;
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		try {
			configService.evaluateEndpoint(request.getRequestURI());
			response = pageService.save(params, headers);
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

	@GetMapping(path = "/users")
	@CrossOrigin(origins = "*")
	public ResponseEntity<IEmulator> getUsers(HttpServletRequest request, @RequestHeader HttpHeaders headers) {
		HttpStatus status = HttpStatus.OK;
		IEmulator response = null;
		HttpHeaders headersTx = new HttpHeaders();
		try {
			configService.evaluateEndpoint(request.getRequestURI());
			response = pageService.getUsers(headers);
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

	@ExceptionHandler({ Exception.class, EmulatorException.class })
	public ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
		Object bodyOfResponse = ex.getMessage();
		logger.info("Salto la Excepción !!!!");
		if (ex instanceof EmulatorException) {
			bodyOfResponse = new ErrorData(((EmulatorException) ex).getCode(), ((EmulatorException) ex).getMessage());
		}
		return new ResponseEntity<>(bodyOfResponse, HttpStatus.CONFLICT);
	}

}
