package cl.ionix.emulator.interfaces;

import org.springframework.http.HttpHeaders;

import cl.ionix.emulator.dto.EdrPayAuthorizeRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentAuthorizeResponseDTO;
import cl.ionix.emulator.dto.EdrPaymentCreateCryptogramRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentCreateCryptogramResponseDTO;
import cl.ionix.emulator.dto.EdrPaymentReverseRequestDTO;
import cl.ionix.emulator.dto.EdrPaymentReverseResponseDTO;
import cl.ionix.emulator.utils.EmulatorException;

public interface ITransactions {
	public EdrPaymentAuthorizeResponseDTO createTransaction(EdrPayAuthorizeRequestDTO transaction,
			HttpHeaders headerRx) throws EmulatorException;
	
	public EdrPaymentCreateCryptogramResponseDTO createCriptogram(EdrPaymentCreateCryptogramRequestDTO dataCreate,
			HttpHeaders headerRx, String token ) throws EmulatorException;
	
	public EdrPaymentReverseResponseDTO reverseTransaction(EdrPaymentReverseRequestDTO request,
			HttpHeaders headerRx) throws EmulatorException;
	
}
