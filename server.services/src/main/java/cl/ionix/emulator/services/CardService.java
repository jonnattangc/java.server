package cl.ionix.emulator.services;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.ionix.emulator.Card;
import cl.ionix.emulator.Device;
import cl.ionix.emulator.daos.IDaoCard;
import cl.ionix.emulator.daos.IDaoDevice;
import cl.ionix.emulator.dto.ActivatePaymentMethodResponseDTO;
import cl.ionix.emulator.dto.CardInfo;
import cl.ionix.emulator.dto.EdrTokenEnrollmentRequestDTO;
import cl.ionix.emulator.dto.EdrTokenEnrollResponseDTO;
import cl.ionix.emulator.dto.EdrTokenGetDigitalPanRequestDTO;
import cl.ionix.emulator.dto.EdrTokenGetDigitalPanResponseDTO;
import cl.ionix.emulator.dto.EdrTokenGetTokenResponseDTO;
import cl.ionix.emulator.dto.ErrorData;
import cl.ionix.emulator.dto.GetPaymentMethodInfoResponseDTO;
import cl.ionix.emulator.dto.ints.IEmulator;
import cl.ionix.emulator.interfaces.ICard;
import cl.ionix.emulator.interfaces.IUtilities;
import cl.ionix.emulator.utils.EmulatorException;

@Service
public class CardService implements ICard {

	private final static Logger logger = Logger.getLogger(CardService.class.getName());

	@Autowired
	private IDaoCard cardRepository;

	@Autowired
	private IDaoDevice deviceRepository;

	@Autowired
	private IUtilities util;

	@Override
	@Transactional
	public EdrTokenGetDigitalPanResponseDTO cardSearch(EdrTokenGetDigitalPanRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException {
		logger.info("Service de card Search");
		EdrTokenGetDigitalPanResponseDTO response = new EdrTokenGetDigitalPanResponseDTO();
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			String requestid = headerRx.get("requestid").get(0);
			String client_id = headerRx.get("client_id").get(0);
			String access_token = headerRx.get("access_token").get(0);

			logger.info("requestid   : " + requestid);
			logger.info("client_id   : " + client_id);
			logger.info("access_token: " + access_token);
			logger.info("Request Body  : " + body);
			logger.info("Request Header: " + header);

			Long rId = Long.parseLong(request.getRequestorInfo().getRid());
			String dataCard = request.getCardInfo().getData().getProfile();
			String cardNumber = "**** **** **** ****";
			// Card Info es un json que trae el numero de tarjeta...
			logger.info("CardInfo: " + util.decryptRSA(dataCard));
			Map<String, Object> map = util.toMap(util.decryptRSA(dataCard));
			String tokenCard = util.getTokenCard(client_id);
			if (map != null) {
				cardNumber = (String) map.get("fpan");
				if (cardNumber != null)
					tokenCard = util.getTokenCard(cardNumber);
			}
			// busca la tarjeta inscrita
			Card card = cardRepository.findByCardNumber(cardNumber);
			if (card == null) {
				logger.info("No se encuentra tarjeta " + cardNumber + " en PSEUDO-APICET, se ingresa.");
				// Para efecto de pruebas, la registramos a nombre del cliente y retornamos
				// como si estuviera.
				card = new Card();
				card.setToken(tokenCard);
				card.setRId(rId);
				card.setRequest(body);
				card.setProfile(dataCard);
				card.setCardNumber(cardNumber);
				card.setClient(client_id);
				cardRepository.save(card);
			}

			logger.info("Tarjeta: " + cardNumber + " Token: " + card.getToken());
			CardInfo cardInfo = new CardInfo();
			cardInfo.setReference(card.getToken());
			response.setCardInfo(cardInfo);

		} catch (Exception e) {
			logger.severe("Error en servicio de busqueda: " + e.getMessage());
			throw new EmulatorException(e.getMessage() != null ? e.getMessage() : "Error desconocido");
		}
		return response;
	}

	@Override
	@Transactional
	public IEmulator processTNP(HttpServletRequest request, HttpHeaders headerRx) throws EmulatorException {
		logger.info("Proceso de TNP");
		IEmulator response = null;
		try {
			EActions action = EActions.NONE;
			String header = util.toJson(headerRx);
			String auth = headerRx.get("Authorization").get(0);
			auth = auth.replace("Bearer ", "");

			String x_client_id = headerRx.get("X-Client-Id").get(0);
			String x_client_secret = headerRx.get("X-Client-Secret").get(0);
			String dataInUrl = request.getRequestURI().replace("/api/v1/foods/cards/", "");
			if (dataInUrl.indexOf("actions") > 0) {
				action = EActions.getAction(dataInUrl);
				if (!action.equals(EActions.NONE))
					dataInUrl = dataInUrl.replace(action.getEnd(), "");
				dataInUrl = dataInUrl.trim();
			}

			logger.info("Original        : " + dataInUrl);
			String token = URLDecoder.decode(dataInUrl, StandardCharsets.UTF_8.toString());
			logger.info("Token           : " + token);
			String cardNumber = util.decryptAES(token);

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
			logger.info("Request Header  : " + header);
			logger.info("Card            : " + cardNumber);
			logger.info("X-Client-Id     : " + x_client_id);
			logger.info("X-Client-Secret : " + x_client_secret);
			logger.info("Authorization   : " + auth);

			Card card = cardRepository.findByCardNumber(cardNumber);
			String tokenCard = util.SHA256(cardNumber);

			if (card == null) {
				Card entity = new Card();
				entity.setCardNumber(cardNumber);
				entity.setProfile(auth);
				entity.setRequest(header);
				entity.setClient("TBD");
				entity.setRut("TBD");
				entity.setPin("TBD");
				entity.setRId(-1L);
				entity.setAmount(100000000L);
				entity.setToken(tokenCard);
				card = cardRepository.save(entity);
			}
			switch (action) {
			case NONE:
				response = processSearchTNP(token, card);
				break;
			case ASSIGN_PIN:
				response = processAssignPinTNP(card, body);
				break;
			default:
				response = new ErrorData("23421", "No hay infomación");
			}
		} catch (Exception e) {
			logger.severe("Error en servicio TNP: " + e.getMessage());
			throw new EmulatorException(e.getMessage() != null ? e.getMessage() : "Error TNP");
		}
		return response;
	}

	private ActivatePaymentMethodResponseDTO processAssignPinTNP(Card card, String body) {
		ActivatePaymentMethodResponseDTO response = new ActivatePaymentMethodResponseDTO();
		Map<String, Object> map = util.toMap(body);
		String cipherPin = (String) map.get("pin");
		String pin = util.decryptAES(cipherPin);
		String rut = (String) map.get("beneficiary_rut");

		ActivatePaymentMethodResponseDTO.InfoData infoData = new ActivatePaymentMethodResponseDTO.InfoData();
		ActivatePaymentMethodResponseDTO.InfoMeta infoMeta = new ActivatePaymentMethodResponseDTO.InfoMeta();

		if (pin != null && rut != null) {
			card.setRut(rut);
			card.setPin(pin);
			cardRepository.save(card);
			infoData.setStatus(true);
			infoMeta.setStatus("succeeded");
			String[] strs = { "EMULATOR", "EDG", "SERVICES" };
			infoMeta.setMessages(strs);
		}
		response.setData(infoData);
		response.setMeta(infoMeta);
		return response;
	}

	/**
	 * Procesa busqueda de tarjetas TNP
	 * 
	 * @param tokenCipher
	 * @param card
	 * @return
	 */
	private GetPaymentMethodInfoResponseDTO processSearchTNP(final String tokenCipher, final Card card) {
		GetPaymentMethodInfoResponseDTO response = new GetPaymentMethodInfoResponseDTO();
		GetPaymentMethodInfoResponseDTO.InfoData data = new GetPaymentMethodInfoResponseDTO.InfoData();
		GetPaymentMethodInfoResponseDTO.InfoMeta meta = new GetPaymentMethodInfoResponseDTO.InfoMeta();
		data.setActivated(true);
		data.setAvailableBalance(card.getAmount().doubleValue());
		data.setCardNumber(tokenCipher);
		data.setTokenizedCardNumber(card.getToken());
		data.setBeneficiaryRut(card.getRut()); // SIEMPRE -n
		data.setHasObservation(false);
		data.setBordero(1000L);
		data.setMaskedCardNumber("EMULATOR-CARD-TNP-1111");
		response.setData(data);

		String[] strs = { "EMULATOR", "EDG", "SERVICES" };
		meta.setStatus("ACTIVE");
		meta.setMessages(strs);
		response.setMeta(meta);
		return response;
	}

	@Override
	public EdrTokenGetTokenResponseDTO getToken(String token, HttpHeaders headerRx) throws EmulatorException {
		logger.info("Service getToken");
		EdrTokenGetTokenResponseDTO response = new EdrTokenGetTokenResponseDTO();
		try {
			String requestid = headerRx.get("requestid").get(0);
			String client_id = headerRx.get("client_id").get(0);
			String access_token = headerRx.get("access_token").get(0);

			logger.info("requestid   : " + requestid);
			logger.info("client_id   : " + client_id);
			logger.info("access_token: " + access_token);
			// el token es del device
			Device device = deviceRepository.findByToken(token);
			if (device != null) {
				logger.info("Dispositivo encontrado encontrado: " + token);
				EdrTokenGetTokenResponseDTO.TokenInfo tokeninfo = new EdrTokenGetTokenResponseDTO.TokenInfo();
				EdrTokenGetTokenResponseDTO.Profile profile = new EdrTokenGetTokenResponseDTO.Profile();
				// con el card asociado al enrolamiento
				Card card = cardRepository.findByToken(device.getCard());
				if (card != null) {
					String toCipher = "{\"dpan\":\""+ card.getCardNumber()+"\",\"cvv\":\"123\",\"dexp\":\"12/21\"}";
					logger.info("DATA: " + toCipher);					
					profile.setProfile( util.encryptRSA(toCipher) );
					tokeninfo.setData(profile);
					tokeninfo.setReference(device.getToken());
					tokeninfo.setStatus("ACTIVE");
					response.setTokeninfo(tokeninfo);
				} else
					throw new EmulatorException("No existe tarjeta para token", "5001");
			} else
				throw new EmulatorException("No existe tarjeta para token", "5001");

		} catch (Exception e) {
			logger.severe("Error: " + e.getMessage());
			throw new EmulatorException(e.getMessage() != null ? e.getMessage() : "Error desconocido");
		}
		return response;
	}
	
	@Override
	@Transactional
	public EdrTokenEnrollResponseDTO enrollDevice(EdrTokenEnrollmentRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException {
		logger.info("Service de enrolado de tarjetas");
		EdrTokenEnrollResponseDTO response = new EdrTokenEnrollResponseDTO();
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			String requestid = headerRx.get("requestid").get(0);
			String client_id = headerRx.get("client_id").get(0);
			String access_token = headerRx.get("access_token").get(0);

			logger.info("requestid   : " + requestid);
			logger.info("client_id   : " + client_id);
			logger.info("access_token: " + access_token);

			logger.info("Request Body  : " + body);
			logger.info("Request Header: " + header);

			String dataCard = request.getCardInfo().getData().getProfile();

			String cardNumber = "**** **** **** ****";
			logger.info("CardInfo: " + util.decryptRSA(dataCard));
			Map<String, Object> map = util.toMap(util.decryptRSA(dataCard));
			
			String tokenCard = util.getTokenCard(client_id);
			if (map != null) {
				cardNumber = (String) map.get("fpan");
				if (cardNumber != null)
					tokenCard = util.getTokenCard(cardNumber);
			}
			logger.info("CARD: " + cardNumber + " TOKEN: " + tokenCard);
			if (cardNumber.equals("**** **** **** ****")) {
				throw new EmulatorException("Tarjeta no existe, el metodo search dijo puras mentiras", "5000");
			} else {
				Card card = cardRepository.findByCardNumber(cardNumber);
				if (card != null) {
					logger.info("Los token de tarjetas son: " + (tokenCard.equals(card.getToken()) ? "IGUALES" : "DISTINTOS"));
					if (card.getToken().equals(tokenCard)) {
						Device device = deviceRepository.findByCard(card.getToken());
						if (device != null) {
							logger.info("Dispositivo ya existe, el token es: " + device.getToken());
							EdrTokenEnrollResponseDTO.TokenInfo tokenInfo = new EdrTokenEnrollResponseDTO.TokenInfo();
							tokenInfo.setHref("EMULATOR-REF");
							tokenInfo.setReference(device.getToken());
							tokenInfo.setReason("REASON-EMULATOR ");
							tokenInfo.setStatus("ACTIVE");
							response.setTokenInfo(tokenInfo);

							CardInfo cardInfo = new CardInfo();
							cardInfo.setReference(device.getToken());
							response.setCardInfo(cardInfo);
							response.setReference(card.getToken());
							
						} else {
							String tokenDevice = util.SHA256(body);
							logger.info("Dispositivo nuevo, el token es: " + tokenDevice);

							device = new Device();
							device.setRequest(body);

							device.setToken(tokenDevice);
							device.setCard(card.getToken());
							deviceRepository.save(device);

							EdrTokenEnrollResponseDTO.TokenInfo tokenInfo = new EdrTokenEnrollResponseDTO.TokenInfo();
							tokenInfo.setHref("EMULATOR-REF");
							tokenInfo.setReference(tokenDevice);
							tokenInfo.setReason("REASON-EMULATOR ");
							tokenInfo.setStatus("ACTIVE");
							response.setTokenInfo(tokenInfo);

							CardInfo cardInfo = new CardInfo();
							cardInfo.setReference(card.getToken());
							response.setCardInfo(cardInfo);
							response.setReference(card.getToken());
						}
					}
				}

			}

		} catch (Exception e) {
			logger.severe("Error: " + e.getMessage());
			throw new EmulatorException(e.getMessage());
		}

		return response;
	}

}
