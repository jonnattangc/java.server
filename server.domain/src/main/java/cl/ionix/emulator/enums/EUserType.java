package cl.ionix.emulator.enums;

public enum EUserType {
	NONE("Sin tipo"),
	ADMIN("Administrador"),
	NORMAL("Normal"),
	SECURITY("Seguridad");
	
	private final String name;
	
	public String getName() {
		return this.name;
	}
	
	private EUserType( String name ) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static EUserType getTypeByName( final String name ) {
		for( EUserType type : EUserType.values() )
			if( type.getName().equalsIgnoreCase(name) )
				return type;
		return EUserType.NONE;
	}
}
