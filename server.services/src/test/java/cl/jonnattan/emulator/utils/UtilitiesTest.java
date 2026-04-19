package cl.jonnattan.emulator.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.jonnattan.emulator.interfaces.ICipher;
import cl.jonnattan.emulator.interfaces.ISignature;

@ExtendWith(MockitoExtension.class)
class UtilitiesTest {

	@Mock
	private EncrypterBusinessLogicService rsaCipher;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private ICipher aesCipher;
	@Mock
	private ISignature signature;

	@InjectMocks
	private Utilities utilities;

	@Test
	void cksumSHA256_retornaLong() {
		when(signature.cksum("data")).thenReturn(100L);
		long result = utilities.cksumSHA256("data");
		assertTrue(result > 100L);
	}

	@Test
	void sha256_delegaEnSignature() {
		when(signature.getSignature("x")).thenReturn("HASH");
		assertEquals("HASH", utilities.SHA256("x"));
	}

	@Test
	void decryptRSA_dataNull_retornaVacio() {
		assertEquals("", utilities.decryptRSA(null));
	}

	@Test
	void decryptRSA_error_retornaVacio() throws Exception {
		when(rsaCipher.decrypt(any())).thenThrow(new RuntimeException("boom"));
		String result = utilities.decryptRSA("YQ=="); // "a" en base64
		assertEquals("", result);
	}

	@Test
	void decryptRSA_ok_retornaTexto() throws Exception {
		when(rsaCipher.decrypt(any())).thenReturn("hola".getBytes());
		String result = utilities.decryptRSA("YQ==");
		assertEquals("hola", result);
	}

	@Test
	void encryptRSA_dataNull_retornaVacio() {
		assertEquals("", utilities.encryptRSA(null));
	}

	@Test
	void encryptRSA_ok_retornaBase64() throws Exception {
		when(rsaCipher.encrypt(any())).thenReturn("hola".getBytes());
		String result = utilities.encryptRSA("hola");
		assertNotNull(result);
		assertTrue(result.length() > 0);
	}

	@Test
	void encryptRSA_error_retornaVacio() throws Exception {
		when(rsaCipher.encrypt(any())).thenThrow(new RuntimeException("boom"));
		assertEquals("", utilities.encryptRSA("hola"));
	}

	@Test
	void toJson_objNull_retornaNULL() {
		assertEquals("NULL", utilities.toJson(null));
	}

	@Test
	void toJson_ok_serializa() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"a\":1}");
		assertEquals("{\"a\":1}", utilities.toJson(new Object()));
	}

	@Test
	void toJson_error_retornaTextoError() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {
			private static final long serialVersionUID = 1L;
		});
		assertEquals("ERROR Convirtiendo a JSON", utilities.toJson(new Object()));
	}

	@Test
	void toMap_error_retornaNull() throws Exception {
		when(objectMapper.readValue(any(String.class), any(Class.class))).thenThrow(new NullPointerException("boom"));
		assertEquals(null, utilities.toMap("{}"));
	}

	@Test
	void toMap_ok_retornaMap() throws Exception {
		Map<String, Object> expected = new HashMap<>();
		expected.put("k", "v");
		when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn(expected);
		assertEquals(expected, utilities.toMap("{}"));
	}

	@Test
	void decryptAES_null_retornaVacio() {
		assertEquals("", utilities.decryptAES(null));
	}

	@Test
	void decryptAES_ok_retornaPlano() throws Exception {
		when(aesCipher.decrypt("CIPHER")).thenReturn("PLANO");
		assertEquals("PLANO", utilities.decryptAES("CIPHER"));
	}

	@Test
	void decryptAES_error_retornaVacio() throws Exception {
		when(aesCipher.decrypt(any())).thenThrow(new RuntimeException("boom"));
		assertEquals("", utilities.decryptAES("CIPHER"));
	}

	@Test
	void getTokenCard_retornaVeinteCaracteres() {
		when(signature.getSignature(any()))
				.thenReturn("abcdef0123456789abcdef0123456789abcdef0123456789");
		String result = utilities.getTokenCard("data");
		assertEquals(20, result.length());
	}
}
