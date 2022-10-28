package cl.ionix.emulator.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.ionix.emulator.User;
import cl.ionix.emulator.daos.IDaoUser;
import cl.ionix.emulator.interfaces.IEdr;
import cl.ionix.emulator.interfaces.IUtilities;
import cl.ionix.emulator.utils.EmulatorException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class EdrService implements IEdr {

	private final static Logger logger = Logger.getLogger(EdrService.class.getName());

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

			logger.info("name : " + name + " pass: " + pass + " realm: " + realm);
			logger.info("Request Header: " + header);

			String textRealLogin = realLogin(name, pass, realm);

			if (textRealLogin != null)
				accesstoken = textRealLogin;
			User user = userRepository.findByNameUserAndPassword(name, pass);
			if (user != null) {
				user.setAccessToken(accesstoken);
				userRepository.save(user);
				logger.info("#### User Id: " + user.getId() );
			} else {
				user = new User();
				user.setNameUser(name);
				user.setPassword(pass);
				user.setRealm(realm);
				user.setAccessToken(accesstoken);
				logger.info("Se guarda...: " + name );
				userRepository.save(user);
			}

			if (textRealLogin == null) {
				long ms = System.currentTimeMillis() + (24L * 3600L * 1000L);
				response = String.format("wrap_access_token=%s&wrap_access_token_expires_in=%d", accesstoken,
						(ms / 1000));
			} else {
				response = accesstoken;
			}
			logger.info("response: " + accesstoken);
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
			logger.info("endpoint: " + url);
			HttpEntity<String> response = restTemplateWithTimeout.postForEntity(url, request, String.class);
			logger.info("Response: " + util.toJson(response));
			if (response.getBody() == null || !response.getBody().contains("wrap_access_token")) {
				return null;
			}
			return response.getBody();
		} catch (NumberFormatException e) {
			logger.severe(e.getMessage());
			return null;
		} catch (HttpStatusCodeException hsce) {
			logger.severe("Code[" + hsce.getStatusCode() + "]: " + hsce.getMessage());
			if (hsce.getStatusCode() == HttpStatus.UNAUTHORIZED)
				return null;
			return null;
		} catch (org.springframework.web.client.RestClientException e) {
			logger.severe(e.getMessage());
			return null;
		}
	}

}
