package cl.ionix.emulator.interfaces;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

import cl.ionix.emulator.dto.EdrTokenEnrollmentRequestDTO;
import cl.ionix.emulator.dto.EdrTokenEnrollResponseDTO;
import cl.ionix.emulator.dto.EdrTokenGetDigitalPanRequestDTO;
import cl.ionix.emulator.dto.EdrTokenGetDigitalPanResponseDTO;
import cl.ionix.emulator.dto.EdrTokenGetTokenResponseDTO;
import cl.ionix.emulator.dto.ints.IEmulator;
import cl.ionix.emulator.utils.EmulatorException;

public interface ICard {

	public EdrTokenGetDigitalPanResponseDTO cardSearch(EdrTokenGetDigitalPanRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException;

	public IEmulator processTNP(HttpServletRequest request, HttpHeaders headerRx) throws EmulatorException;

	public EdrTokenGetTokenResponseDTO getToken(String token, HttpHeaders headerRx) throws EmulatorException;

	public EdrTokenEnrollResponseDTO enrollDevice(EdrTokenEnrollmentRequestDTO request,
			HttpHeaders headerRx ) throws EmulatorException;
}
