package cl.ionix.emulator.services;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.ionix.emulator.Card;
import cl.ionix.emulator.Device;
import cl.ionix.emulator.Transaction;
import cl.ionix.emulator.daos.IDaoCard;
import cl.ionix.emulator.daos.IDaoDevice;
import cl.ionix.emulator.daos.IDaoTransaction;
import cl.ionix.emulator.dto.EdrPayAuthorizeRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentAuthorizeResponseDTO;
import cl.ionix.emulator.dto.EdrPaymentCreateCryptogramRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentCreateCryptogramResponseDTO;
import cl.ionix.emulator.dto.EdrPaymentReverseRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentReverseResponseDTO;
import cl.ionix.emulator.enums.TransactionStatus;
import cl.ionix.emulator.interfaces.ITransactions;
import cl.ionix.emulator.interfaces.IUtilities;
import cl.ionix.emulator.utils.EmulatorException;
import cl.ionix.emulator.utils.UtilConst;

@Service
public class TransactionService implements ITransactions {

	private final static Logger logger = Logger.getLogger(TransactionService.class.getName());

	@Autowired
	private IDaoTransaction transactionRepository;
	@Autowired
	private IDaoCard cardRepository;
	@Autowired
	private IDaoDevice deviceRepository;

	@Autowired
	private IUtilities util;

	@Override
	@Transactional
	public EdrPaymentAuthorizeResponseDTO createTransaction(EdrPayAuthorizeRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException {
		logger.info("Service de transacciones");

		EdrPaymentAuthorizeResponseDTO response = new EdrPaymentAuthorizeResponseDTO();
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			logger.info("Request Body: " + body);
			logger.info("Request Header: " + header);
			String dataCard = request.getToken().getData();

			String cardNumber = UtilConst.cardDefault;

			Map<String, Object> map = util.toMap(util.decryptRSA(dataCard));
			if (map != null) {
				String value = (String) map.get("cryptogram");
				if( value != null && value.startsWith(UtilConst.prefijoCyptogram) )
				{
					logger.info("Criptograma: " + value);
					Device device = deviceRepository.findByCryptogram(value);
					if (device != null) {
						logger.info("Card: " + device.getCard());
						Card card = cardRepository.findByToken(device.getCard());
						if (card != null)
							cardNumber = card.getCardNumber();
					}else
						logger.info("####### dispositivo no encontrado");
				}
			}
			logger.info("CardInfo: " + util.decryptRSA(dataCard));
			logger.info("Card: " + cardNumber);

			String idJson = util.toJson(request.getTransaction());
			String authId = String.format("%d", util.cksumSHA256(idJson));
			String amount = request.getTransaction().getAmount();
			logger.info("Realiza transacción por $" + amount + " a la tarjeta: " + cardNumber);
			Transaction transaction = new Transaction();
			transaction.setJsonId(idJson);
			transaction.setAuthorizationId(authId);
			transaction.setData(dataCard);
			transaction.setRequest(body);
			transaction.setStatus(TransactionStatus.AUTHORIZED);
			transaction.setCard(cardNumber);
			transaction.setAmount(amount);
			transactionRepository.save(transaction);

			if (!cardNumber.equals(UtilConst.cardDefault)) {
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
			trans.setStatus("APPROVED");
			trans.setAvailablebalance("1005260.0");
			trans.setResponsecode("200");
			trans.setAuthnumber(authId);

			response.setTransactionex(trans);

			response.setRequestorInfo(request.getRequestorInfo());

		} catch (Exception e) {
			logger.severe("Error: " + e.getMessage());
			throw new EmulatorException("Error creando transacción", "5544");
		}
		return response;
	}

	@Override
	@Transactional
	public EdrPaymentReverseResponseDTO reverseTransaction(EdrPaymentReverseRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException {

		EdrPaymentReverseResponseDTO response = new EdrPaymentReverseResponseDTO();
		logger.info("Servicio de reversa");
		try {
			String body = util.toJson(request);
			String header = util.toJson(headerRx);

			logger.info("Request Body: " + body);
			logger.info("Request Header: " + header);

			String idTransaction = request.getTransactionVoid().getAuthorization().getId();

			Transaction transaction = transactionRepository.findByAuthorizationId(idTransaction);
			if (transaction != null) {
				if (transaction.getStatus().equals(TransactionStatus.AUTHORIZED)) {
					transactionRepository.saveStatusById(TransactionStatus.REVERSED, transaction.getId(), new Date());
					response.setRequestorInfo(request.getRequestorInfo());
					response.setTransactionVoid(request.getTransactionVoid());

					// Esto es lo que importa para IONIX
					EdrPaymentReverseResponseDTO.Transactionex trans = new EdrPaymentReverseResponseDTO.Transactionex();
					trans.setStatus("APPROVED");
					trans.setAvailablebalance("1005260.0");
					trans.setResponsecode("200");
					trans.setAuthnumber(idTransaction);
					trans.setMcc("EMULATOR-MCC");
					response.setTransactionex(trans);
					// se devuelve la plata a la tarjeta
					Card card = cardRepository.findByCardNumber( transaction.getCard() );
					if(card != null ) {
						Long value = card.getAmount();
						value += Long.parseLong(transaction.getAmount());
						trans.setAvailablebalance(String.format("%.1f", (double)value));
						cardRepository.saveAmountById(value, card.getId(), new Date() );
					}

				} else
					throw new EmulatorException("Transaction invalid status");
			} else
				throw new EmulatorException("Transaction not found");

		} catch (Exception e) {
			logger.severe("Error: " + e.getMessage());
			throw new EmulatorException("Error reversando transacción", "32441");
		}

		return response;
	}

	@Override
	@Transactional
	public EdrPaymentCreateCryptogramResponseDTO createCriptogram(
			EdrPaymentCreateCryptogramRequestDTO dataCreate, HttpHeaders headerRx, String token)
			throws EmulatorException {

		EdrPaymentCreateCryptogramResponseDTO response = new EdrPaymentCreateCryptogramResponseDTO();
		logger.info("Service de criptograma");
		try {
			String body = util.toJson(dataCreate);
			String header = util.toJson(headerRx);
			String cryptogram = UtilConst.prefijoCyptogram + util.SHA256(body);
			logger.info("Request Body: " + body);
			logger.info("Request Header: " + header);
			Device device = deviceRepository.findByToken(token);
			if (device != null) {
				deviceRepository.saveCryptogramById(cryptogram, device.getId(), new Date());
			}
			EdrPaymentCreateCryptogramResponseDTO.TokenInfo tokenInfo = new EdrPaymentCreateCryptogramResponseDTO.TokenInfo();
			EdrPaymentCreateCryptogramResponseDTO.CripData data = new EdrPaymentCreateCryptogramResponseDTO.CripData();
			data.setAtc("ATC-EMULATOR");
			data.setCriptogram(cryptogram);
			tokenInfo.setData(data);
			tokenInfo.setReference("REF-EMULATOR");
			tokenInfo.setStatus("OK");
			response.setTokenInfo(tokenInfo);

		} catch (Exception e) {
			logger.severe("Error: " + e.getMessage());
			throw new EmulatorException("Error creando criptograma", "5423");
		}
		return response;
	}

}
