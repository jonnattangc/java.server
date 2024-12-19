package cl.jonnattan.emulator.services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.jonnattan.emulator.dto.cxp.CxpResponseDTO;
import cl.jonnattan.emulator.dto.cxp.ICxpResponse;
import cl.jonnattan.emulator.interfaces.ICxp;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;
import cl.jonnattan.emulator.utils.UtilConst;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class CxpService implements ICxp {

	private static final Logger logger = LoggerFactory.getLogger(CxpService.class);

	private enum Enviroments {
		STAGING, DEVELOP, PRODUCTION
	}

	@Autowired
	private RestTemplate restTemplateWithTimeout;

	@Autowired
	private IUtilities util;

	@Value("${cxp.urls}")
	private String address;
	private String[] urls;

	@Value("${cxp.otkeys}")
	private String keysOt;
	private String[] otKey;

	@Value("${cxp.geokeys}")
	private String keysGeo;
	private String[] geokeys;

	@Value("${cxp.cotkeys}")
	private String keysCotizacion;
	private String[] cotkeys;

	@Value("${cxp.select:1}")
	private int index;

	private Enviroments enviroment = Enviroments.DEVELOP;

	@PostConstruct
	public void init() {
		enviroment = Enviroments.values()[index - 1];

		address = address.replace(" ", "");
		address = address.replace("\t", "");
		address = address.replace("\n", "");
		urls = address.split(",");

		keysOt = keysOt.replace(" ", "");
		keysOt = keysOt.replace("\t", "");
		keysOt = keysOt.replace("\n", "");
		otKey = keysOt.split(",");

		keysGeo = keysGeo.replace(" ", "");
		keysGeo = keysGeo.replace("\t", "");
		keysGeo = keysGeo.replace("\n", "");
		geokeys = keysGeo.split(",");

		keysCotizacion = keysCotizacion.replace(" ", "");
		keysCotizacion = keysCotizacion.replace("\t", "");
		keysCotizacion = keysCotizacion.replace("\n", "");
		cotkeys = keysCotizacion.split(",");

	}

	@Override
	public ICxpResponse processPostRequest(HttpServletRequest request, HttpHeaders header) throws EmulatorException {

		CxpResponseDTO response = null;

		try {
			String body = printHeaderAndPayload(request, header);
			String uri = request.getRequestURI();
			uri = uri.replace("/cxp", "");
			List<String> list = header.get(UtilConst.CXP_SUSB_KEY);
			String key = list != null && !list.isEmpty() ? list.get(0) : "";
			String newKey = key;
			HttpHeaders headersTx = new HttpHeaders();
			headersTx.setContentType(MediaType.APPLICATION_JSON);

			if (uri.contains("/rating/api/v1.0/rates/courier")) {
				newKey = cotkeys[enviroment.ordinal()];
			}

			if (uri.contains("/transport-orders/api/v1.0/transport-orders")
					|| uri.contains("/transport-orders/api/v1.0/tracking")) {
				newKey = otKey[enviroment.ordinal()];

			}

			headersTx.set(UtilConst.CXP_SUSB_KEY, newKey);

			HttpEntity<String> requestTx = new HttpEntity<>(body, headersTx);

			String url = urls[enviroment.ordinal()] + uri;
			String msg = String.format("Enviroment %s - Use ApiKey[%s]",enviroment, newKey);
			logger.info("{}", msg);
			StringBuilder text = new StringBuilder("keys: ");
			for (String skey : geokeys)
				text.append(String.format("%s ",skey));
			logger.info("{}", text);

			msg = String.format("Endpoint Proxy: %s",url);
			logger.info("{}", msg);

			long init = System.currentTimeMillis();
			HttpEntity<CxpResponseDTO> res = restTemplateWithTimeout.postForEntity(url, requestTx,
					CxpResponseDTO.class);
			long diff = ((System.currentTimeMillis() - init) / 1000L);

			msg = String.format("Response %d seg from CXP: %s ", diff, util.toJson(res));
			logger.info("{}", msg);

			response = res.getBody();

		} catch (Exception e) {
			e.printStackTrace();
			throw new EmulatorException("Error de comunicacion");
		}

		return response;
	}

	/**
	 * Imprime en pantalla
	 * 
	 * @param request
	 * @param header
	 * @throws IOException
	 */
	private String printHeaderAndPayload(final HttpServletRequest request, final HttpHeaders header)
			throws IOException {

		int dato = -1;
		ServletInputStream sis = request.getInputStream();
		ByteBuffer bb = ByteBuffer.allocate(4096);
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

		body = body.replace(" ", "");
		body = body.replace("\n", "");
		body = body.replace("\t", "");

		logger.info("Request URI: {}", request.getRequestURI());
		String msg = String.format("Request Header Rx: %s",util.toJson(header));
		logger.info("{}", msg);
		msg = String.format("Request Body Rx  : %s",body);
		logger.info("{}", msg);

		return body;

	}

	@Override
	public String processGetRequest(HttpServletRequest request, HttpHeaders header) throws EmulatorException {
		logger.info("Request URI: {}", request.getRequestURI());

		String response = "";
		String uri = request.getRequestURI();
		try {

			if (uri.contains("/dev") || uri.contains("/develop")) {
				response = String.format("Enviroment change from %s to Develop",enviroment.name());
				enviroment = Enviroments.DEVELOP;
			} else if (uri.contains("/qa") || uri.contains("/quality") || uri.contains("/staging")) {
				response = String.format("Enviroment change from %s to Staging",enviroment.name());
				enviroment = Enviroments.STAGING;
			} else if (uri.contains("/prod") && uri.contains("/production")) {
				response = String.format("Enviroment change from %s to Production",enviroment.name());
				enviroment = Enviroments.PRODUCTION;
			} else {
				response = String.format("Enviroment not change. Actually is %s",enviroment.name());
				throw new EmulatorException(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Response: {}", response);
		return response;
	}

}
