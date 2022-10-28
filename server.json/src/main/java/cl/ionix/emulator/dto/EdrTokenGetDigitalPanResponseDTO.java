package cl.ionix.emulator.dto;

import cl.ionix.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrTokenGetDigitalPanResponseDTO implements IEmulator{
	CardInfo cardInfo;
}
