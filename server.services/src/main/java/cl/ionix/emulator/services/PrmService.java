package cl.ionix.emulator.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.ionix.emulator.dto.EdrPaymentLogonRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentLogonResponseDTO;
import cl.ionix.emulator.interfaces.IPrm;
import cl.ionix.emulator.utils.EmulatorException;

@Service
public class PrmService implements IPrm {

	private final static Logger logger = Logger.getLogger(PrmService.class.getName());

	@Autowired
	ObjectMapper objectMapper;
	
	@Override
	public EdrPaymentLogonResponseDTO logon(String requestor, HttpHeaders headerRx,
			EdrPaymentLogonRequestDTO request) throws EmulatorException {
		logger.info("Servicio LOGON Prm");
		try {
			String body = objectMapper.writeValueAsString(request);
			String header = objectMapper.writeValueAsString(headerRx);
			
			String requestid = headerRx.get("requestid").get(0);
			String client_id = headerRx.get("client_id").get(0);
			String access_token = headerRx.get("access_token").get(0);
			
			logger.info("requestid   : " + requestid);
			logger.info("client_id   : " + client_id);
			logger.info("access_token: " + access_token);
			
			logger.info("Request Body  : " + body);
			logger.info("Request Header: " + header);
			
		} catch (Exception e) {
			logger.severe("Error: " + e.getMessage());
			throw new EmulatorException(e.getMessage());
		}
		return new EdrPaymentLogonResponseDTO();
	}

}
