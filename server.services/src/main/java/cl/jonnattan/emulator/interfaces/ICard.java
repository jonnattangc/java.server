package cl.jonnattan.emulator.interfaces;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.dto.EdrTokenEnrollResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenEnrollmentRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetDigitalPanResponseDTO;
import cl.jonnattan.emulator.dto.EdrTokenGetTokenResponseDTO;
import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.utils.EmulatorException;

public interface ICard {

	public EdrTokenGetDigitalPanResponseDTO cardSearch(EdrTokenGetDigitalPanRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException;

	public IEmulator processTNP(HttpServletRequest request, HttpHeaders headerRx) throws EmulatorException;

	public EdrTokenGetTokenResponseDTO getToken(String token, HttpHeaders headerRx) throws EmulatorException;

	public EdrTokenEnrollResponseDTO enrollDevice(EdrTokenEnrollmentRequestDTO request,
			HttpHeaders headerRx ) throws EmulatorException;
}
