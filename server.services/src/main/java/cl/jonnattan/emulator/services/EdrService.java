package cl.jonnattan.emulator.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.jonnattan.emulator.User;
import cl.jonnattan.emulator.daos.IDaoUser;
import cl.jonnattan.emulator.interfaces.IEdr;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;

@Service
public class EdrService implements IEdr {

	private static final Logger logger = LoggerFactory.getLogger(EdrService.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IDaoUser userRepository;

	@Autowired
	private IUtilities util;

	@Autowired
	private RestTemplate restTemplateWithTimeout;

	@Transactional
	@Override
	public String loginEdenred(MultiValueMap<String, String> headerRx) throws EmulatorException {

		logger.info("Service de login");
		String response = "";
		try {
			String header = objectMapper.writeValueAsString(headerRx);

			String name = headerRx.get("wrap_name").get(0);
			String pass = headerRx.get("wrap_password").get(0);
			String realm = headerRx.get("realm").get(0);
			// inicio con un valor al azar
			String accesstoken = util.SHA256(header);
			String msg = String.format("name: %s pass: %s realm: %s", name, pass, realm);
			logger.info("{}", msg);
			logger.info("Request Header: {}", header);

			String textRealLogin = realLogin(name, pass, realm);

			if (textRealLogin != null)
				accesstoken = textRealLogin;
			User user = userRepository.findByNameUserAndPassword(name, pass);
			if (user != null) {
				user.setAccessToken(accesstoken);
				userRepository.save(user);
				logger.info("#### User Id: {}", user.getId());
			} else {
				user = new User();
				user.setNameUser(name);
				user.setPassword(pass);
				user.setRealm(realm);
				user.setAccessToken(accesstoken);
				logger.info("Se guarda...: {}", name);
				userRepository.save(user);
			}

			if (textRealLogin == null) {
				long ms = System.currentTimeMillis() + (24L * 3600L * 1000L);
				response = String.format("wrap_access_token=%s&wrap_access_token_expires_in=%d", accesstoken,(ms / 1000));
			} else {
				response = accesstoken;
			}
			logger.info("response: {}", accesstoken);
		} catch (Exception e) {
			throw new EmulatorException("No se puede logear user", "1223");
		}
		return response;
	}

	/**
	 * Realiza el login en la pagina
	 * 
	 * @param name
	 * @param pass
	 * @param realm
	 * @return
	 */
	private String realLogin(final String name, final String pass, final String realm) {
		String body = null;
		try {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			httpHeaders.set("sf_persistent", "false");
			httpHeaders.set("deflate", "true");
			httpHeaders.set("wrap_name", name);
			httpHeaders.set("wrap_password", pass);
			httpHeaders.set("realm", realm);
			// ticket x defecto
			String url = "https://certificacion.edenred.cl/EdenredPrivateWebPortal/sitefinity/Authenticate/SWT";
			if (realm.contains("EdenredJunaebWebPortal"))
				url = "https://certificacion.edenred.cl/EdenredJunaebWebPortal/Sitefinity/Authenticate/SWT";

			HttpEntity<?> request = new HttpEntity<>(httpHeaders);
			logger.info("endpoint: {} ", url);
			HttpEntity<String> response = restTemplateWithTimeout.postForEntity(url, request, String.class);
			String msg = String.format("%s",util.toJson(response));
			logger.info("Response: {}", msg);

			body = response.getBody();
			if (body == null || !body.contains("wrap_access_token"))
				body = null;

		} catch (NumberFormatException e) {
			logger.error("Error", e);

		} catch (HttpStatusCodeException hsce) {
			logger.error("Error Http", hsce);

		} catch (org.springframework.web.client.RestClientException restEx) {
			logger.error("Error Http", restEx);

		}
		return body;
	}

}
