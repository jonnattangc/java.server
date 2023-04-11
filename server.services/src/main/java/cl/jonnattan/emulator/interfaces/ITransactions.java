package cl.jonnattan.emulator.interfaces;

import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.dto.EdrPayAuthorizeRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentAuthorizeResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentCreateCryptogramResponseDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseRequestDTO;
import cl.jonnattan.emulator.dto.EdrPaymentReverseResponseDTO;
import cl.jonnattan.emulator.utils.EmulatorException;

public interface ITransactions {
	public EdrPaymentAuthorizeResponseDTO createTransaction(EdrPayAuthorizeRequestDTO transaction,
			HttpHeaders headerRx) throws EmulatorException;
	
	public EdrPaymentCreateCryptogramResponseDTO createCriptogram(EdrPaymentCreateCryptogramRequestDTO dataCreate,
			HttpHeaders headerRx, String token ) throws EmulatorException;
	
	public EdrPaymentReverseResponseDTO reverseTransaction(EdrPaymentReverseRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException;
	
}
