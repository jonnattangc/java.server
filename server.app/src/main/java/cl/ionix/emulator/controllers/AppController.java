package cl.ionix.emulator.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ionix.emulator.dto.AppConfigurationRequestDTO;
import cl.ionix.emulator.interfaces.IConfigurations;
import cl.ionix.emulator.utils.ConfException;

/**
 * Controlles de conf de emulador
 * 
 * @author Jonnattan Griffiths
 * @since Programa EMULADOR
 * @version 1.0 del 22-06-2020
 * 
 */
@RestController
@RequestMapping("/emulator/config")
public class AppController {

	private static final Logger logger = LoggerFactory.getLogger(AppController.class);
	private static final String text = " para enpoint: ";
	private static final String linea = "---------------------------------------------------------------------------------------";
	@Autowired
	private IConfigurations configService;

	@PostMapping(value = "/update")
	public ResponseEntity<String> update(@Valid @RequestBody AppConfigurationRequestDTO request) {
		logger.info(linea);
		HttpStatus status = HttpStatus.OK;
		String success = "Se ha actualizado el endpoint: " + request.getEndPoint();
		try {
			success = configService.updateConfigurations(request);
		} catch (ConfException e) {
			status = e.getStatus();
			success = e.getMessage() + text + request.getEndPoint();
		}
		return new ResponseEntity<>(success, status);
	}

	@PostMapping(value = "/create")
	public ResponseEntity<String> create(@Valid @RequestBody AppConfigurationRequestDTO request) {
		logger.info(linea);
		HttpStatus status = HttpStatus.OK;
		String success = "Se ha creado configuración para endpoint: " + request.getEndPoint();
		try {
			success = configService.createConfigurations(request);
		} catch (ConfException e) {
			status = e.getStatus();
			success = e.getMessage() + text + request.getEndPoint();
		}
		return new ResponseEntity<>(success, status);
	}

	@PostMapping(value = "/save")
	public ResponseEntity<String> save(@Valid @RequestBody AppConfigurationRequestDTO request) {
		logger.info(linea);
		HttpStatus status = HttpStatus.OK;
		String success = "Se guardará el endpoint: " + request.getEndPoint();
		try {
			success = configService.saveConfigurations(request);
		} catch (ConfException e) {
			status = e.getStatus();
			success = e.getMessage() + text + request.getEndPoint();
		}
		return new ResponseEntity<>(success, status);
	}
}
