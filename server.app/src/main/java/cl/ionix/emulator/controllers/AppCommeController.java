package cl.ionix.emulator.controllers;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ionix.emulator.dto.CommerceMailResponseDTO;
import cl.ionix.emulator.dto.CommerceTokenResponseDTO;

/**
 * Controlles respuesta a la APPs Cmc
 * 
 * @author Jonnattan Griffiths
 * @since Programa EMULADOR
 * @version 1.0 del 30-09-2020
 * 
 */
@RestController
@RequestMapping("/commerceapps")
public class AppCommeController {

	private final static Logger logger = Logger.getLogger(AppCommeController.class.getName());

	@PostMapping(path = "/sendMail")
	public ResponseEntity<CommerceMailResponseDTO> sendMail(HttpServletRequest request,
			@RequestHeader MultiValueMap<String, String> headersRx) {
		CommerceMailResponseDTO response = new CommerceMailResponseDTO();
		logger.info("****************************/commerceapps/sendMail****************************");

		try {
			int dato = -1;
			ServletInputStream sis = request.getInputStream();
			ByteBuffer bb = ByteBuffer.allocate(1024);
			while ((dato = sis.read()) != -1)
				bb.put((byte) dato);
			bb.flip();
			int len = bb.remaining();
			String body = "";
			if (len > 0) {
				byte[] bytes = new byte[len];
				bb.get(bytes);
				body = new String(bytes);
			}
			bb = null;

			logger.info("Request Body    : " + body);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return new ResponseEntity<CommerceMailResponseDTO>(response, HttpStatus.OK);
	}

	@PostMapping(path = "/getToken", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<CommerceTokenResponseDTO> getToken(@RequestHeader MultiValueMap<String, String> headersRx) {
		logger.info("****************************/commerceapps/getToken****************************");
		CommerceTokenResponseDTO token = new CommerceTokenResponseDTO();
		token.setAccess_token("1111111222222333334444455555666677788899");
		token.setExpires_in(10);
		token.setToken_type("Emnulado");
		return new ResponseEntity<CommerceTokenResponseDTO>(token, HttpStatus.OK);
	}
}
