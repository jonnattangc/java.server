package cl.jonnattan.emulator.controllers;


import cl.jonnattan.emulator.dto.AppListConfigurationDTOResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import cl.jonnattan.emulator.dto.AppConfigurationRequestDTO;
import cl.jonnattan.emulator.interfaces.IConfigurations;
import cl.jonnattan.emulator.utils.ConfException;
import cl.jonnattan.emulator.utils.UtilConst;
import jakarta.validation.Valid;


/**
 * Controller de configuraci'on de emulador
 * Sirve para configurar como queremos que respondan los servicios
 * ac'a definidos
 *
 * @author Jonnattan Griffiths
 * @since Programa EMULADOR
 * @version 1.0 del 22-06-2020
 *
 */
@RestController
@RequestMapping("/config")
public class AppController {

	private static final Logger logger = LoggerFactory.getLogger(AppController.class);

	private final IConfigurations configService;

	public AppController(IConfigurations configService) {
		this.configService = configService;
	}

	@PostMapping(value = "/update")
	public ResponseEntity<String> update(@Valid @RequestBody AppConfigurationRequestDTO request) {
		logger.info(UtilConst.LINE);
		HttpStatus status = HttpStatus.OK;
		String success = "Se ha actualizado el endpoint: " + request.getEndPoint();
		try {
			success = configService.updateConfigurations(request);
		} catch (ConfException e) {
			status = e.getStatus();
			success = e.getMessage() + UtilConst.TEXT_TO_ENDPOINT + request.getEndPoint();
		}
		return new ResponseEntity<>(success, status);
	}

	@PostMapping(value = "/create")
	public ResponseEntity<String> create(@Valid @RequestBody AppConfigurationRequestDTO request) {
		logger.info(UtilConst.LINE);
		HttpStatus status = HttpStatus.OK;
		String success = "Se ha creado configuración para endpoint: " + request.getEndPoint();
		try {
			success = configService.createConfigurations(request);
		} catch (ConfException e) {
			status = e.getStatus();
			success = e.getMessage() +  UtilConst.TEXT_TO_ENDPOINT + request.getEndPoint();
		}
		return new ResponseEntity<>(success, status);
	}

	@PostMapping(value = "/save")
	public ResponseEntity<String> save(@Valid @RequestBody AppConfigurationRequestDTO request) {
		logger.info(UtilConst.LINE);
		HttpStatus status = HttpStatus.OK;
		String success = "Se guardará el endpoint: " + request.getEndPoint();
		try {
			success = configService.saveConfigurations(request);
		} catch (ConfException e) {
			status = e.getStatus();
			success = e.getMessage() +  UtilConst.TEXT_TO_ENDPOINT + request.getEndPoint();
		}
		return new ResponseEntity<>(success, status);
	}

	@GetMapping(value = "/list")
	public ResponseEntity<AppListConfigurationDTOResponse> list(){
		logger.info(UtilConst.LINE);
		HttpStatus status = HttpStatus.OK;
		AppListConfigurationDTOResponse response = null;
		try {
			response = configService.getConfigurations();
		} catch (ConfException e) {
			status = e.getStatus();
		}
		return new ResponseEntity<>(response, status);
	}
}
