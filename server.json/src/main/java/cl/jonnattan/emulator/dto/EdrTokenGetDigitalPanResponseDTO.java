package cl.jonnattan.emulator.dto;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class EdrTokenGetDigitalPanResponseDTO implements IEmulator{
	CardInfo cardInfo;
}
