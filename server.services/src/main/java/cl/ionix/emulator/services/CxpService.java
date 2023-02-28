package cl.ionix.emulator.services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.ionix.emulator.dto.cxp.CxpResponseDTO;
import cl.ionix.emulator.dto.cxp.ICxpResponse;
import cl.ionix.emulator.interfaces.ICxp;
import cl.ionix.emulator.interfaces.IUtilities;
import cl.ionix.emulator.utils.EmulatorException;

@Service
public class CxpService implements ICxp {

	private final static Logger logger = Logger.getLogger(CxpService.class.getName());

	private enum Enviroments {
		Staging, Develop, Production
	};

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

	private Enviroments enviroment = Enviroments.Develop;

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

		//logger.info("Env[" + enviroment + "] Base URL: " + urls[enviroment.ordinal()] + " Size: " + urls.length);
		//logger.info("Env[" + enviroment + "] COT Key : " + cotkeys[enviroment.ordinal()] + " Size: " + cotkeys.length);
		//logger.info("Env[" + enviroment + "] GEO Key : " + geokeys[enviroment.ordinal()] + " Size: " + geokeys.length);
		//logger.info("Env[" + enviroment + "] OT Key  : " + otKey[enviroment.ordinal()] + " Size: " + otKey.length);
	}

	@Override
	public ICxpResponse processPostRequest(HttpServletRequest request, HttpHeaders header) throws EmulatorException {

		CxpResponseDTO response = null;

		try {
			String body = printHeaderAndPayload(request, header);
			String uri = request.getRequestURI();
			uri = uri.replace("/cxp", "");

			String key = header.get("ocp-apim-subscription-key").get(0);
			String newKey = key;
			HttpHeaders headersTx = new HttpHeaders();
			headersTx.setContentType(MediaType.APPLICATION_JSON_UTF8);

			if (uri.contains("/rating/api/v1.0/rates/courier")) {
				newKey = cotkeys[enviroment.ordinal()];
			}

			if (uri.contains("/transport-orders/api/v1.0/transport-orders")
					|| uri.contains("/transport-orders/api/v1.0/tracking")) {
				newKey = otKey[enviroment.ordinal()];

			}

			headersTx.set("ocp-apim-subscription-key", newKey);

			HttpEntity<String> requestTx = new HttpEntity<>(body, headersTx);

			String url = urls[enviroment.ordinal()] + uri;

			logger.info(String.format("Enviroment %s - Use ApiKey[%s]", enviroment, newKey));
			logger.info(String.format("Endpoint Proxy: %s", url));

			long init = System.currentTimeMillis();
			HttpEntity<CxpResponseDTO> res = restTemplateWithTimeout.postForEntity(url, requestTx,
					CxpResponseDTO.class);
			long diff = (long) ((System.currentTimeMillis() - init) / 1000L);
			logger.info(String.format("Response %d seg from CXP: %s ", diff, util.toJson(res)));
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
		bb = null;

		body = body.replace(" ", "");
		body = body.replace("\n", "");
		body = body.replace("\t", "");
		logger.info("Request URI:" + request.getRequestURI());
		logger.info(String.format("Request Header Rx: %s", util.toJson(header)));
		logger.info(String.format("Request Body Rx  : %s", body));

		return body;

	}

	@Override
	public String processGetRequest(HttpServletRequest request, HttpHeaders header) throws EmulatorException {
		logger.info("Request URI:" + request.getRequestURI());

		String response = "";
		String uri = request.getRequestURI();
		try {

			if (uri.equals("/cxp/magento")) {
				logger.info("Response: /cxp/wix");
				response = String.format("/cxp/wix OK");
			} else if (uri.contains("/cxp/wix/install")) {
				logger.info("Response: /cxp/wix/install");
				response = String.format("/cxp/wix/install OK");
			} else if (uri.contains("/cxp/wix/close")) {
				logger.info("Response: /cxp/wix/close");
				response = String.format("/cxp/wix/close OK");
			} else if (uri.contains("/cxp/wix/other")) {
				logger.info("Response: /cxp/wix/other");
				response = String.format("/cxp/wix/other OK");
			} else if (uri.contains("/dev") || uri.contains("/develop")) {
				response = String.format("Enviroment change from %s to Develop", enviroment.name());
				enviroment = Enviroments.Develop;
			} else if (uri.contains("/qa") || uri.contains("/quality") || uri.contains("/staging")) {
				response = String.format("Enviroment change from %s to Staging", enviroment.name());
				enviroment = Enviroments.Staging;
			} else if (uri.contains("/prod") && uri.contains("/production")) {
				response = String.format("Enviroment change from %s to Production", enviroment.name());
				enviroment = Enviroments.Production;
			} else {
				response = String.format("Enviroment not change. Actually is %s", enviroment.name());
				throw new EmulatorException(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Response:" + response);
		return response;
	}

}
