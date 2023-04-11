package cl.jonnattan.emulator.services;

public enum EActions {
	NONE("JONNATTAN"), ASSIGN_PIN("/actions/assign-pin");
	private String endAct;
	
	private EActions(String action ) {
		this.endAct = action;
	}
	
	public String getEnd() {
		return this.endAct;
	}
	public static EActions getAction( final String uri ) {
		for( EActions action : values() )
			if( uri.endsWith( action.getEnd() ))
				return action;
		return EActions.NONE;
	}
}
