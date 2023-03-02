package cl.ionix.emulator.interfaces;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import cl.ionix.emulator.dto.ints.IEmulator;
import cl.ionix.emulator.utils.EmulatorException;

public interface IPage {

	public IEmulator getUsers(final HttpHeaders header) throws EmulatorException;

	public IEmulator save(final MultiValueMap<String, String> params, final HttpHeaders header)
			throws EmulatorException;
}
