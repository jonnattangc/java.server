package cl.ionix.emulator.utils;

import java.io.IOException;
import java.util.Map;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.ionix.emulator.interfaces.ICipher;
import cl.ionix.emulator.interfaces.ISignature;
import cl.ionix.emulator.interfaces.IUtilities;

@Component
public class Utilities implements IUtilities {

	@Autowired
	private EncrypterBusinessLogicService rsaCipher;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ICipher aesCipher;

	@Autowired
	private ISignature signature;

	@Override
	public long cksumSHA256(String data) {
		return (System.currentTimeMillis() + signature.cksum(data));
	}

	@Override
	public String SHA256(String data) {
		String result = signature.getSignature(data);
		return result;
	}

	@Override
	public String decryptRSA(String data) {
		String result = "";
		if (data != null) {
			try {
				byte[] encryptedData = Base64.decode(data);
				byte[] decryptedData = rsaCipher.decrypt(encryptedData);
				result = new String(decryptedData);
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
			}
		}
		return result;
	}

	@Override
	public String encryptRSA(String data) {
		String result = "";
		if (data != null) {
			try {
				byte[] clearData = data.getBytes();
				byte[] encryptData = rsaCipher.encrypt(clearData);
				byte[] bytes = Base64.encode(encryptData);
				result = new String(bytes);

			} catch (Exception e) {
				e.printStackTrace();
				result = "";
			}
		}
		return result;
	}

	@Override
	public String toJson(Object obj) {
		String resp = "";
		try {
			resp = (obj != null) ? objectMapper.writeValueAsString(obj) : "NULL";
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			resp = "ERROR Convirtiendo a JSON";
		}
		return resp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> toMap(String json) {
		Map<String, Object> map = null;
		try {
			map = (Map<String, Object>) objectMapper.readValue(json, Map.class);
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public String decryptAES(String data) {
		String result = "";
		if (data != null) {
			try {
				result = aesCipher.decrypt(data);
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
			}
		}

		return result;
	}

	@Override
	public String getTokenCard(String data) {
		String sha = SHA256(data);
		return sha.toUpperCase().substring(0, 20);
	}
}
