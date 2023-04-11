package cl.jonnattan.emulator.interfaces;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.dto.cxp.ICxpResponse;
import cl.jonnattan.emulator.utils.EmulatorException;

public interface ICxp {
	/**
	 * Procesa lo de Cxp
	 * @param request
	 * @param headerRx
	 * @return
	 * @throws EmulatorException
	 */
	public ICxpResponse processPostRequest(HttpServletRequest request, HttpHeaders header) throws EmulatorException;
	
	/**
	 * Procesa los metodos GET
	 * @param request
	 * @param header
	 * @return
	 * @throws EmulatorException
	 */
	public String processGetRequest(HttpServletRequest request, HttpHeaders header) throws EmulatorException;
}
