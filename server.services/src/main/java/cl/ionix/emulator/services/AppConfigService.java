package cl.ionix.emulator.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.ionix.emulator.Configuration;
import cl.ionix.emulator.daos.IDaoConfiguration;
import cl.ionix.emulator.dto.AppConfigurationRequestDTO;
import cl.ionix.emulator.enums.TypeResponse;
import cl.ionix.emulator.interfaces.IConfigurations;
import cl.ionix.emulator.utils.ConfException;

@Service
public class AppConfigService implements IConfigurations {

	private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);

	@Autowired
	private IDaoConfiguration configRepository;

	@Override
	@Transactional
	public String updateConfigurations(AppConfigurationRequestDTO request) throws ConfException {
		logger.info("Service para actualizar configuracion");
		String response = "Se actualiza configuración";
		try {
			Configuration conf = configRepository.findByEndpoint(request.getEndPoint());
			if (conf != null) {
				if (request.getError().booleanValue()) {
					conf.setCode(request.getCode());
					conf.setMessage(request.getMessage());
					conf.setType(TypeResponse.getType(request.getType()));
					conf.setError(request.getError());
				} else {
					conf.setCode(null);
					conf.setMessage(null);
					conf.setType(TypeResponse.HTTP_RESPONSE_200);
					conf.setError(false);
				}
				logger.info("************** UPDATE CONFIGURACION **************** ");
				logger.info("Se reporta Código error: {}", conf.getCode());
				logger.info("Se reporta Mensaje error: {}", conf.getMessage());
				logger.info("Se reporta Tipo error: {}", conf.getType());
				logger.info("************************************************* ");
				configRepository.save(conf);
			} else
				createConfigurations(request);
		} catch (Exception e) {
			throw new ConfException("[" + e.getMessage() + "] Actualizando Configuración");
		}
		return response;
	}

	@Override
	@Transactional
	public String createConfigurations(AppConfigurationRequestDTO request) throws ConfException {
		logger.info("Service para crear configuracion");
		String response = "Se crea configuración";
		try {
			Configuration conf = new Configuration();
			conf.setEndpoint(request.getEndPoint());

			if (request.getError().booleanValue()) {
				conf.setCode(request.getCode());
				conf.setMessage(request.getMessage());
				conf.setType(TypeResponse.getType(request.getType()));
				conf.setError(request.getError());
			} else {
				conf.setCode(null);
				conf.setMessage(null);
				conf.setType(TypeResponse.HTTP_RESPONSE_200);
				conf.setError(false);
			}
			logger.info("************** CREATE CONFIGURACION **************** ");
			logger.info("Se reporta Código error: {} ", conf.getCode());
			logger.info("Se reporta Mensaje error: {} ", conf.getMessage());
			logger.info("Se reporta Tipo error: {}", conf.getType());
			logger.info("************************************************* ");
			configRepository.save(conf);

		} catch (Exception e) {
			throw new ConfException("[" + e.getMessage() + "] Creando Configuración");
		}
		return response;
	}

	@Override
	@Transactional
	public String saveConfigurations(AppConfigurationRequestDTO request) throws ConfException {
		logger.info("Service para guardar configuracion");
		String response = "Se guarda configuración";
		try {
			Configuration conf = configRepository.findByEndpoint(request.getEndPoint());
			if (conf != null) {
				response = updateConfigurations(request);
			} else
				response = createConfigurations(request);
		} catch (Exception e) {
			throw new ConfException(e.getMessage());
		}
		return response;
	}
	
	@SuppressWarnings("null")
	@Override
	public void evaluateEndpoint(String endpoint) throws ConfException {
		Configuration configuration = configRepository.findByEndpoint(endpoint);
		if (configuration != null && configuration.getError().booleanValue() ) {
			logger.info("************** REPORTA ERROR POR CONFIGURACION **************** ");
			logger.info("Se reporta Código error: {} ", configuration.getCode());
			logger.info("Se reporta Mensaje error: {}", configuration.getMessage());
			logger.info("Se reporta Tipo error: {}", configuration.getType());
			logger.info("*************************************************************** ");
			// se provoca el internal error para quien consume el servicio
			if (configuration.getType().equals(TypeResponse.HTTP_RESPONSE_500)) {
				String nulo = null;
				nulo.equals("nada");
			}
			throw new ConfException(configuration.getMessage(), configuration.getCode(), configuration.getType());
		}
	}


}
