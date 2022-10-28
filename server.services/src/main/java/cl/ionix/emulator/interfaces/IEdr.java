package cl.ionix.emulator.interfaces;

import org.springframework.util.MultiValueMap;

import cl.ionix.emulator.utils.EmulatorException;

public interface IEdr {
	public String loginEdenred( MultiValueMap<String,String> headerRx) throws EmulatorException;
}
