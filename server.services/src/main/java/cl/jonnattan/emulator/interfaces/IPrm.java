package cl.jonnattan.emulator.interfaces;

import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.dto.EdrPaymentLogonRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentLogonResponseDTO;
import cl.jonnattan.emulator.utils.EmulatorException;

public interface IPrm {
	public EdrPaymentLogonResponseDTO logon(String requestor, HttpHeaders header,
			EdrPaymentLogonRequestDTO request) throws EmulatorException;
}
