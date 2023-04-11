package cl.jonnattan.emulator.interfaces;

import cl.jonnattan.emulator.dto.AppConfigurationRequestDTO;
import cl.jonnattan.emulator.utils.ConfException;

public interface IConfigurations {
	
	public String saveConfigurations(AppConfigurationRequestDTO request) throws ConfException;
	
	public String updateConfigurations(AppConfigurationRequestDTO request) throws ConfException;

	public String createConfigurations(AppConfigurationRequestDTO request) throws ConfException;

	public void evaluateEndpoint(String endpoint) throws ConfException;
}
