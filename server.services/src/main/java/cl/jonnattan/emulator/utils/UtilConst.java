package cl.jonnattan.emulator.utils;

public class UtilConst {

	private UtilConst() {
		throw new IllegalStateException("Utility class");
	}

	public static final String CRYPTO_PREF = "CRIPTOGRAM-EMULATOR-";
	public static final String DEFAULT_CARD = "XXXXXXXXXXXXXXXX";
	public static final String MASKED_CARD = "**** **** **** ****";
	public static final String NO_INFO = "Sin Información";
	public static final String TEXT_TO_ENDPOINT = " para enpoint: ";
	public static final String CXP_SUSB_KEY = "ocp-apim-subscription-key";
	public static final String SAVE_INFO_TO = "Se guarda informacion de ";
	public static final String X_CLIENT_ID = "X-Client-Id";
	public static final String X_CLIENT_SECRET = "X-Client-Secret";
	public static final String REQUEST_ID = "requestid";
	public static final String CLIENT_ID = "client_id";
	public static final String ACCESS_TOKEN = "access_token";

	public static final String STATUS_APPROVED = "APPROVED";
	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String RESPONSE_CODE_200 = "200";
	public static final String DEFAULT_BALANCE = "1005260.0";
	public static final String EMULATOR_MCC = "EMULATOR-MCC";
	public static final String ATC_EMULATOR = "ATC-EMULATOR";
	public static final String REF_EMULATOR = "EMULATOR-REF";
	public static final String REASON_EMULATOR = "REASON-EMULATOR ";

	public static final String LINE = "---------------------------------------------------------------------------------------";
}
