package cl.jonnattan.emulator.dto;

import lombok.Data;
import java.util.Date;

@Data
public class AppConfigurationResponseDTO extends AppConfigurationRequestDTO {
    private Date lastUpdate;
}
