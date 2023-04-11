package cl.jonnattan.emulator.interfaces;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.utils.EmulatorException;

public interface IPage {

	public IEmulator getUsers(final HttpHeaders header) throws EmulatorException;

	public IEmulator save(final MultiValueMap<String, String> params, final HttpHeaders header)
			throws EmulatorException;
}
