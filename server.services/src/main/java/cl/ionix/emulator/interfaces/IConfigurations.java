package cl.ionix.emulator.interfaces;

import cl.ionix.emulator.dto.AppConfigurationRequestDTO;
import cl.ionix.emulator.utils.ConfException;

public interface IConfigurations {
	
	public String saveConfigurations(AppConfigurationRequestDTO request) throws ConfException;
	
	public String updateConfigurations(AppConfigurationRequestDTO request) throws ConfException;

	public String createConfigurations(AppConfigurationRequestDTO request) throws ConfException;

	public void evaluateEndpoint(String endpoint) throws ConfException;
}
