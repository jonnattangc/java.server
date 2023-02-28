package cl.ionix.emulator.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.ionix.emulator.dto.EdrPaymentLogonRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentLogonResponseDTO;
import cl.ionix.emulator.interfaces.IPrm;
import cl.ionix.emulator.utils.EmulatorException;
import cl.ionix.emulator.utils.UtilConst;

@Service
public class PrmService implements IPrm {

	private static final Logger logger = LoggerFactory.getLogger(PrmService.class);

	@Autowired
	ObjectMapper objectMapper;

	@Override
	public EdrPaymentLogonResponseDTO logon(String requestor, HttpHeaders headerRx, EdrPaymentLogonRequestDTO request)
			throws EmulatorException {
		logger.info("Servicio LOGON Prm");
		try {
			String body = objectMapper.writeValueAsString(request);
			String header = objectMapper.writeValueAsString(headerRx);

			List<String> list = headerRx.get("requestid");
			String requestId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get("client_id");
			String clientId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get("access_token");
			String accessToken = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;

			logger.info("requestid   : {}", requestId);
			logger.info("client_id   : {}", clientId);
			logger.info("access_token: {}", accessToken);

			logger.info("Request Body  : {}", body);
			logger.info("Request Header: {}", header);

		} catch (Exception e) {
			throw new EmulatorException(e.getMessage());
		}
		return new EdrPaymentLogonResponseDTO();
	}

}
