package cl.jonnattan.emulator.interfaces;

public interface ISignature {
	public String getSignature( final String data );
	public long cksum( final String data );
}
