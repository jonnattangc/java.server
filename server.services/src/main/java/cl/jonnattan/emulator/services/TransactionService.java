package cl.jonnattan.emulator.services;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.jonnattan.emulator.Card;
import cl.jonnattan.emulator.Device;
import cl.jonnattan.emulator.Transaction;
import cl.jonnattan.emulator.daos.IDaoCard;
import cl.jonnattan.emulator.daos.IDaoDevice;
import cl.jonnattan.emulator.daos.IDaoTransaction;
import cl.jonnattan.emulator.dto.EdrPayAuthorizeRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentAuthorizeResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseResponseDTO;
import cl.jonnattan.emulator.enums.TransactionStatus;
import cl.jonnattan.emulator.interfaces.ITransactions;
import cl.jonnattan.emulator.interfaces.IUtilities;
import cl.jonnattan.emulator.utils.EmulatorException;
import cl.jonnattan.emulator.utils.UtilConst;

@Service
public class TransactionService implements ITransactions {

	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	private final IDaoTransaction transactionRepository;
	private final IDaoCard cardRepository;
	private final IDaoDevice deviceRepository;
	private final IUtilities util;

	public TransactionService(IDaoTransaction transactionRepository, IDaoCard cardRepository,
			IDaoDevice deviceRepository, IUtilities util) {
		this.transactionRepository = transactionRepository;
		this.cardRepository = cardRepository;
		this.deviceRepository = deviceRepository;
		this.util = util;
	}

	@Override
	@Transactional
	public EdrPaymentAuthorizeResponseDTO createTransaction(EdrPayAuthorizeRequestDTO request, HttpHeaders headerRx)
			throws EmulatorException {
		logger.info("Service de transacciones");

		EdrPaymentAuthorizeResponseDTO response = new EdrPaymentAuthorizeResponseDTO();
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			logger.info("Request Body  : {}", body);
			logger.info("Request Header: {}", header);

			String dataCard = request.getToken().getData();

			String cardNumber = UtilConst.DEFAULT_CARD;

			Map<String, Object> map = util.toMap(util.decryptRSA(dataCard));
			if (map != null) {
				String value = (String) map.get("cryptogram");
				if (value != null && value.startsWith(UtilConst.CRYPTO_PREF)) {
					logger.info("Criptograma: {}", value);
					Device device = deviceRepository.findByCryptogram(value);
					if (device != null) {
						logger.info("Card: {}", device.getCard());
						Card card = cardRepository.findByToken(device.getCard());
						if (card != null) {
							cardNumber = card.getCardNumber();
						}
					} else {
						logger.info("####### dispositivo no encontrado");
					}
				}
			}
			String msg = util.decryptRSA(dataCard);
			logger.info("CardInfo: {}", msg);
			logger.info("Card: {}", cardNumber);

			String idJson = util.toJson(request.getTransaction());
			String authId = String.format("%d",util.cksumSHA256(idJson));
			String amount = request.getTransaction().getAmount();
			msg = String.format("Realiza transacción por $%s a la tarjeta: %s",amount, cardNumber);
			logger.info("{}", msg);
			Transaction transaction = new Transaction();
			transaction.setJsonId(idJson);
			transaction.setAuthorizationId(authId);
			transaction.setData(dataCard);
			transaction.setRequest(body);
			transaction.setStatus(TransactionStatus.AUTHORIZED);
			transaction.setCard(cardNumber);
			transaction.setAmount(amount);
			transactionRepository.save(transaction);

			if (!cardNumber.equals(UtilConst.DEFAULT_CARD)) {
				Card card = cardRepository.findByCardNumber(cardNumber);
				Long valor = Long.parseLong(amount);
				if (card != null) {
					valor = card.getAmount() - valor;
					cardRepository.saveAmountById(valor, card.getId(), new Date());
				}
			}
			// Se responde lo mismo, esto identifica la transaccion de manera unica
			response.setTransaction(request.getTransaction());
			// Esto es lo que importa para IONIX
			EdrPaymentAuthorizeResponseDTO.Transactionex trans = new EdrPaymentAuthorizeResponseDTO.Transactionex();
			trans.setStatus(UtilConst.STATUS_APPROVED);
			trans.setAvailablebalance(UtilConst.DEFAULT_BALANCE);
			trans.setResponsecode(UtilConst.RESPONSE_CODE_200);
			trans.setAuthnumber(authId);

			response.setTransactionex(trans);

			response.setRequestorInfo(request.getRequestorInfo());

		} catch (Exception e) {
			logger.error("Error", e);
			throw new EmulatorException("Error creando transacción", "5544");
		}
		return response;
	}

	@Override
	@Transactional
	public EdrPaymentReverseResponseDTO reverseTransaction(EdrPaymentReverseRequestDTO request, HttpHeaders headerRx)
			throws EmulatorException {

		EdrPaymentReverseResponseDTO response = new EdrPaymentReverseResponseDTO();
		logger.info("Servicio de reversa");
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			logger.info("Request Body: {}", body);
			logger.info("Request Header: {}", header);

			String idTransaction = request.getTransactionVoid().getAuthorization().getId();

			Transaction transaction = transactionRepository.findByAuthorizationId(idTransaction);
			if (transaction == null) {
				throw new EmulatorException("Transaction not found");
			}
			if (!transaction.getStatus().equals(TransactionStatus.AUTHORIZED)) {
				throw new EmulatorException("Transaction invalid status");
			}
			transactionRepository.saveStatusById(TransactionStatus.REVERSED, transaction.getId(), new Date());
			response.setRequestorInfo(request.getRequestorInfo());
			response.setTransactionVoid(request.getTransactionVoid());

			// Esto es lo que importa para IONIX
			EdrPaymentReverseResponseDTO.Transactionex trans = new EdrPaymentReverseResponseDTO.Transactionex();
			trans.setStatus(UtilConst.STATUS_APPROVED);
			trans.setAvailablebalance(UtilConst.DEFAULT_BALANCE);
			trans.setResponsecode(UtilConst.RESPONSE_CODE_200);
			trans.setAuthnumber(idTransaction);
			trans.setMcc(UtilConst.EMULATOR_MCC);
			response.setTransactionex(trans);
			// se devuelve la plata a la tarjeta
			Card card = cardRepository.findByCardNumber(transaction.getCard());
			if (card != null) {
				Long value = card.getAmount();
				value += Long.parseLong(transaction.getAmount());
				trans.setAvailablebalance(String.format("%.1f",(double) value));
				cardRepository.saveAmountById(value, card.getId(), new Date());
			}

		} catch (EmulatorException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error: ", e);
			throw new EmulatorException("Error reversando transacción", "32441");
		}

		return response;
	}

	@Override
	@Transactional
	public EdrPaymentCreateCryptogramResponseDTO createCriptogram(EdrPaymentCreateCryptogramRequestDTO dataCreate,
			HttpHeaders headerRx, String token) throws EmulatorException {

		EdrPaymentCreateCryptogramResponseDTO response = new EdrPaymentCreateCryptogramResponseDTO();
		logger.info("Service de criptograma");
		try {
			String body = util.toJson(dataCreate);
			String header = util.toJson(headerRx);
			String cryptogram = UtilConst.CRYPTO_PREF + util.SHA256(body);
			logger.info("Request Body: {}", body);
			logger.info("Request Header: {}", header);
			Device device = deviceRepository.findByToken(token);
			if (device != null) {
				deviceRepository.saveCryptogramById(cryptogram, device.getId(), new Date());
			}
			EdrPaymentCreateCryptogramResponseDTO.TokenInfo tokenInfo = new EdrPaymentCreateCryptogramResponseDTO.TokenInfo();
			EdrPaymentCreateCryptogramResponseDTO.CripData data = new EdrPaymentCreateCryptogramResponseDTO.CripData();
			data.setAtc(UtilConst.ATC_EMULATOR);
			data.setCriptogram(cryptogram);
			tokenInfo.setData(data);
			tokenInfo.setReference("REF-EMULATOR");
			tokenInfo.setStatus("OK");
			response.setTokenInfo(tokenInfo);

		} catch (Exception e) {
			logger.error("Error: ", e);
			throw new EmulatorException("Error creando criptograma", "5423");
		}
		return response;
	}

}
