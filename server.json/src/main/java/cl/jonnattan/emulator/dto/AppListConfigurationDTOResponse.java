package cl.jonnattan.emulator.dto;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import cl.jonnattan.emulator.dto.AppConfigurationResponseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppListConfigurationDTOResponse implements IEmulator {
    private List<AppConfigurationResponseDTO> configurations = new ArrayList<>();
}
