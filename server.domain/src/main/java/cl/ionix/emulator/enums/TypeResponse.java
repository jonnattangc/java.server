package cl.ionix.emulator.enums;

public enum TypeResponse {
	HTTP_RESPONSE_200(200), 
	HTTP_RESPONSE_400(400), 
	HTTP_RESPONSE_409(409), 
	HTTP_RESPONSE_500(500), 
	HTTP_RESPONSE_503(503);
	
	private int value = 0;
	
	private TypeResponse( int value ) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static TypeResponse getType( Integer value )
	{
		for( TypeResponse type : values() )
			if( type.getValue() == value )
				return type;
		return HTTP_RESPONSE_200;
	}
}
