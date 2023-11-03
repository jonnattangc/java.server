package cl.jonnattan.emulator.services;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.jonnattan.emulator.Card;
import cl.jonnattan.emulator.Device;
import cl.jonnattan.emulator.daos.IDaoCard;
import cl.jonnattan.emulator.daos.IDaoDevice;
import cl.jonnattan.emulator.dto.ActivatePaymentMethodResponseDTO;
import cl.jonnattan.emulator.dto.CardInfo;
import cl.jonnattan.emulator.dto.EdrTokenEnrollResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenEnrollmentRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetTokenResponseDTO;
import cl.jonnattan.emulator.dto.ErrorData;
import cl.jonnattan.emulator.dto.GetPaymentMethodInfoResponseDTO;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.interfaces.ICard;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;
import cl.jonnattan.emulator.utils.UtilConst;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class CardService implements ICard {

	private static final Logger logger = LoggerFactory.getLogger(CardService.class);

	@Autowired
	private IDaoCard cardRepository;

	@Autowired
	private IDaoDevice deviceRepository;

	@Autowired
	private IUtilities util;

	@Override
	@Transactional
	public EdrTokenGetDigitalPanResponseDTO cardSearch(EdrTokenGetDigitalPanRequestDTO request, HttpHeaders headerRx)
			throws EmulatorException {
		logger.info("Service de card Search");
		EdrTokenGetDigitalPanResponseDTO response = new EdrTokenGetDigitalPanResponseDTO();
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			List<String> list = headerRx.get(UtilConst.REQUEST_ID);
			String requestId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get(UtilConst.CLIENT_ID);
			String clientId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get(UtilConst.ACCESS_TOKEN);
			String accessToken = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;

			logger.info("requestid: {} client_id: {} access_token:{}", requestId, clientId, accessToken);

			logger.info("Request Body  : {}", body);
			logger.info("Request Header: {}", header);

			Long rId = Long.parseLong(request.getRequestorInfo().getRid());
			String dataCard = request.getCardInfo().getData().getProfile();
			String cardNumber = "**** **** **** ****";
			// Card Info es un json que trae el numero de tarjeta...
			String msg = util.decryptRSA(dataCard);

			logger.info("CardInfo: {}", msg);
			Map<String, Object> map = util.toMap(util.decryptRSA(dataCard));
			String tokenCard = util.getTokenCard(clientId);
			if (map != null) {
				cardNumber = (String) map.get("fpan");
				if (cardNumber != null)
					tokenCard = util.getTokenCard(cardNumber);
			}
			// busca la tarjeta inscrita
			Card card = cardRepository.findByCardNumber(cardNumber);
			if (card == null) {
				logger.info("No se encuentra tarjeta {} en PSEUDO-APICET, se ingresa.", cardNumber);
				// Para efecto de pruebas, la registramos a nombre del cliente y retornamos
				// como si estuviera.
				card = new Card();
				card.setToken(tokenCard);
				card.setRId(rId);
				card.setRequest(body);
				card.setProfile(dataCard);
				card.setCardNumber(cardNumber);
				card.setClient(clientId);
				cardRepository.save(card);
			}
			msg = String.format("Tarjeta: %s Token: %s", cardNumber, card.getToken());
			logger.info("{}", msg);
			CardInfo cardInfo = new CardInfo();
			cardInfo.setReference(card.getToken());
			response.setCardInfo(cardInfo);

		} catch (Exception e) {
			logger.error("Error en servicio de busqueda: ", e);
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

			String xClientId = headerRx.get(UtilConst.X_CLIENT_ID).get(0);
			String xClientSecret = headerRx.get(UtilConst.X_CLIENT_SECRET).get(0);
			String dataInUrl = request.getRequestURI().replace("/api/v1/foods/cards/", "");

			if (dataInUrl.indexOf("actions") > 0) {
				action = EActions.getAction(dataInUrl);
				if (!action.equals(EActions.NONE))
					dataInUrl = dataInUrl.replace(action.getEnd(), "");
				dataInUrl = dataInUrl.trim();
			}

			logger.info("Original        : {}", dataInUrl);
			String token = URLDecoder.decode(dataInUrl, StandardCharsets.UTF_8.toString());
			logger.info("Token           : {}", token);
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

			logger.info("Request Body    : {}", body);
			logger.info("Request Header  : {}", header);
			logger.info("Card            : {}", cardNumber);
			logger.info("X-Client-Id     : {}", xClientId);
			logger.info("X-Client-Secret : {}", xClientSecret);
			logger.info("Authorization   : {}", auth);

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
			logger.error("Error en servicio TNP: ", e);
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

			List<String> list = headerRx.get(UtilConst.REQUEST_ID);
			String requestId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get(UtilConst.CLIENT_ID);
			String clientId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get(UtilConst.ACCESS_TOKEN);
			String accessToken = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;

			logger.info("requestid: {} client_id: {} access_token:{}", requestId, clientId, accessToken);

			// el token es del device
			Device device = deviceRepository.findByToken(token);
			if (device != null) {
				logger.info("Dispositivo encontrado encontrado: {} ", token);
				EdrTokenGetTokenResponseDTO.TokenInfo tokeninfo = new EdrTokenGetTokenResponseDTO.TokenInfo();
				EdrTokenGetTokenResponseDTO.Profile profile = new EdrTokenGetTokenResponseDTO.Profile();
				// con el card asociado al enrolamiento
				Card card = cardRepository.findByToken(device.getCard());
				if (card != null) {
					String toCipher = "{\"dpan\":\"" + card.getCardNumber() + "\",\"cvv\":\"123\",\"dexp\":\"12/21\"}";
					logger.info("DATA: {}", toCipher);
					profile.setProfile(util.encryptRSA(toCipher));
					tokeninfo.setData(profile);
					tokeninfo.setReference(device.getToken());
					tokeninfo.setStatus("ACTIVE");
					response.setTokeninfo(tokeninfo);
				} else
					throw new EmulatorException("No existe tarjeta para token", "5001");
			} else
				throw new EmulatorException("No existe tarjeta para token", "5001");

		} catch (Exception e) {
			logger.error("Error: ", e);
			throw new EmulatorException(e.getMessage() != null ? e.getMessage() : "Error desconocido");
		}
		return response;
	}

	@Override
	@Transactional
	public EdrTokenEnrollResponseDTO enrollDevice(EdrTokenEnrollmentRequestDTO request, HttpHeaders headerRx)
			throws EmulatorException {
		logger.info("Service de enrolado de tarjetas");
		EdrTokenEnrollResponseDTO response = new EdrTokenEnrollResponseDTO();
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			List<String> list = headerRx.get(UtilConst.REQUEST_ID);
			String requestId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get(UtilConst.CLIENT_ID);
			String clientId = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;
			list = headerRx.get(UtilConst.ACCESS_TOKEN);
			String accessToken = list != null && !list.isEmpty() ? list.get(0) : UtilConst.NO_INFO;

			logger.info("requestid: {} client_id: {} access_token:{}", requestId, clientId, accessToken);

			logger.info("Request Body  : {}", body);
			logger.info("Request Header: {}", header);

			String dataCard = request.getCardInfo().getData().getProfile();

			String cardNumber = "**** **** **** ****";
			String msg = util.decryptRSA(dataCard);
			logger.info("CardInfo: {}", msg);

			Map<String, Object> map = util.toMap(util.decryptRSA(dataCard));

			String tokenCard = util.getTokenCard(clientId);
			if (map != null) {
				cardNumber = (String) map.get("fpan");
				if (cardNumber != null)
					tokenCard = util.getTokenCard(cardNumber);
			}
			logger.info("CARD: {} TOKEN: {}", cardNumber, tokenCard);

			if (cardNumber.equals("**** **** **** ****")) {
				throw new EmulatorException("Tarjeta no existe, el metodo search dijo puras mentiras", "5000");
			} else {
				Card card = cardRepository.findByCardNumber(cardNumber);
				if (card != null) {
					logger.info("Los token de tarjetas son: {}",
							(tokenCard.equals(card.getToken()) ? "IGUALES" : "DISTINTOS"));
					if (card.getToken().equals(tokenCard)) {
						Device device = deviceRepository.findByCard(card.getToken());
						if (device != null) {
							logger.info("Dispositivo ya existe, el token es: {}", device.getToken());
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
							logger.info("Dispositivo nuevo, el token es: {}", tokenDevice);

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
			logger.error("Error: ", e);
			throw new EmulatorException(e.getMessage());
		}

		return response;
	}

}
