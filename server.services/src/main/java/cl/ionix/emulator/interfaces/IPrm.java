package cl.ionix.emulator.interfaces;

import org.springframework.http.HttpHeaders;

import cl.ionix.emulator.dto.EdrPaymentLogonRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentLogonResponseDTO;
import cl.ionix.emulator.utils.EmulatorException;

public interface IPrm {
	public EdrPaymentLogonResponseDTO logon(String requestor, HttpHeaders header,
			EdrPaymentLogonRequestDTO request) throws EmulatorException;
}
