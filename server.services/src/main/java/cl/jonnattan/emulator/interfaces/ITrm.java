package cl.jonnattan.emulator.interfaces;

import org.springframework.http.HttpHeaders;

import cl.jonnattan.emulator.dto.EdrTokenLogonRequestDTO;
import cl.jonnattan.emulator.dto.EdrTokenLogonResponseDTO;
import cl.jonnattan.emulator.utils.EmulatorException;

public interface ITrm {
  public EdrTokenLogonResponseDTO logon( String requetor, HttpHeaders header, EdrTokenLogonRequestDTO request) throws EmulatorException;
}
