package cl.jonnattan.emulator.interfaces;

import org.springframework.util.MultiValueMap;

import cl.jonnattan.emulator.utils.EmulatorException;

public interface IEdr {
	public String loginEdenred( MultiValueMap<String,String> headerRx) throws EmulatorException;
}
