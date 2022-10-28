package cl.ionix.emulator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.ionix.emulator.dto.EdrTokenLogonRequestDTO;
import cl.ionix.emulator.dto.EdrTokenLogonResponseDTO;
import cl.ionix.emulator.interfaces.ITrm;
import cl.ionix.emulator.utils.EmulatorException;

@Service
public class TrmService implements ITrm {

	private final static Logger logger = Logger.getLogger(TrmService.class.getName());

	@Autowired
	ObjectMapper objectMapper;

	@Override
	public EdrTokenLogonResponseDTO logon(String requetor, HttpHeaders headerRx, EdrTokenLogonRequestDTO request)
			throws EmulatorException {
		logger.info("Servicio LOGON Trm");

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
		} catch (JsonProcessingException e) {
			logger.severe("Error: " + e.getMessage());
			throw new EmulatorException(e.getMessage());
		}

		return getTest();
	}

	private EdrTokenLogonResponseDTO getTest() {
		EdrTokenLogonResponseDTO response = new EdrTokenLogonResponseDTO();
		
		List<EdrTokenLogonResponseDTO.Tsp> tsps  = new ArrayList<>();
		EdrTokenLogonResponseDTO.Tsp tsp = new EdrTokenLogonResponseDTO.Tsp();
		tsp.setId("2e936958-9fbf-11e4-89d3-123b93f75cba");
		tsp.setPriority(1);
		tsps.add(tsp);
		
		EdrTokenLogonResponseDTO.Enrollment enroll = new EdrTokenLogonResponseDTO.Enrollment();
		List<String> list = new ArrayList<String>();
		list.add("PAN_16");
		enroll.setParams( list );
		
		List<EdrTokenLogonResponseDTO.Bin> bins = new ArrayList<>();
		//--------------------------------------
		EdrTokenLogonResponseDTO.Bin bin = new EdrTokenLogonResponseDTO.Bin();
		bin.setProduct("Ticket Restaurant");
		bin.setCountry("CL");
		bin.setBinStart("60508290");
		bin.setBinEnd("60508299");
		bin.setTsps( tsps );
		bin.setEnrollment(enroll);
		bins.add(bin);
		//--------------------------------------
		EdrTokenLogonResponseDTO.Bin bin1 = new EdrTokenLogonResponseDTO.Bin();
		bin1.setProduct("Ticket Restaurant JUNAEB");
		bin1.setCountry("CL");
		bin1.setBinStart("60508290");
		bin1.setBinEnd("60508299");
		bin1.setTsps( tsps );
		bin1.setEnrollment(enroll);
		bins.add(bin1);
		//--------------------------------------
		EdrTokenLogonResponseDTO.Bin bin2 = new EdrTokenLogonResponseDTO.Bin();
		bin2.setProduct("Ticket Alimentacion");
		bin2.setCountry("CL");
		bin2.setBinStart("60508290");
		bin2.setBinEnd("60508299");
		bin2.setTsps( tsps );
		bin2.setEnrollment(enroll);
		bins.add(bin2);
		//--------------------------------------
		return response;
	}
}
