package cl.ionix.emulator.interfaces;

import org.springframework.http.HttpHeaders;

import cl.ionix.emulator.dto.EdrTokenLogonRequestDTO;
import cl.ionix.emulator.dto.EdrTokenLogonResponseDTO;
import cl.ionix.emulator.utils.EmulatorException;

public interface ITrm {
  public EdrTokenLogonResponseDTO logon( String requetor, HttpHeaders header, EdrTokenLogonRequestDTO request) throws EmulatorException;
}
