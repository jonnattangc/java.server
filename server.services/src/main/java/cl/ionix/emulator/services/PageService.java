package cl.ionix.emulator.services;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import cl.ionix.emulator.User;
import cl.ionix.emulator.daos.IDaoUser;
import cl.ionix.emulator.dto.ints.IEmulator;
import cl.ionix.emulator.dto.user.UserDTO;
import cl.ionix.emulator.dto.user.UserListDTOResponse;
import cl.ionix.emulator.dto.user.UserListDTOResponse.UserResponse;
import cl.ionix.emulator.dto.user.UserSaveResponse;
import cl.ionix.emulator.enums.EUserType;
import cl.ionix.emulator.interfaces.IPage;
import cl.ionix.emulator.interfaces.IUtilities;
import cl.ionix.emulator.utils.EmulatorException;
import cl.ionix.emulator.utils.UtilConst;

@Service
public class PageService implements IPage {

	private static final Logger logger = LoggerFactory.getLogger(PageService.class);

	@Autowired
	private IDaoUser userRepository;

	@Autowired
	private IUtilities util;

	@Override
	public IEmulator getUsers(final HttpHeaders header) throws EmulatorException {
		IEmulator response = null;
		try {
			Iterable<User> users = userRepository.findAll();
			response = new UserListDTOResponse();
			List<UserResponse> list = ((UserListDTOResponse) response).getUsers();
			for (User user : users)
				list.add(entityToDto(user));
		} catch (Exception e) {
			throw new EmulatorException("Error obteniendo usuario", "6500");
		}
		return response;
	}

	/**
	 * Convierte el Entity en un DTO
	 * 
	 * @param user
	 * @return
	 */
	private UserListDTOResponse.UserResponse entityToDto(final User user) {
		UserListDTOResponse.UserResponse dto = new UserListDTOResponse.UserResponse();
		dto.setId(user.getId());
		dto.setRut(user.getRut());
		dto.setName(user.getFullName());
		dto.setRut(user.getRut());
		dto.setNameUser(user.getNameUser());
		dto.setAge(user.getAge());
		dto.setAddress(user.getAddress());
		dto.setCity(user.getCity());
		dto.setMail(user.getMail());
		dto.setMobile(user.getMobile());
		dto.setType(user.getType().toString());
		return dto;
	}

	@Override
	@Transactional
	public IEmulator save(final MultiValueMap<String, String> params, final HttpHeaders header)
			throws EmulatorException {
		IEmulator response = null;
		try {
			UserDTO dto = new UserDTO();
			dto.setAddress(params.get("address") != null ? params.get("address").get(0) : null);
			dto.setRut(params.get("rut") != null ? params.get("rut").get(0) : null);
			dto.setFullName(params.get("fullName") != null ? params.get("fullName").get(0) : null);
			dto.setNameUser(params.get("userName") != null ? params.get("userName").get(0) : null);
			dto.setAge(params.get("age") != null ? Integer.parseInt(params.get("age").get(0)) : null);
			dto.setCity(params.get("city") != null ? params.get("city").get(0) : null);
			dto.setMail(params.get("mail") != null ? params.get("mail").get(0) : null);
			dto.setMobile(params.get("mobile") != null ? params.get("mobile").get(0) : null);
			dto.setType(params.get("type") != null ? params.get("type").get(0) : null);
			dto.setPass(params.get("pass") != null ? params.get("pass").get(0) : null);

			String msg = new String(dto.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
			logger.info(msg);

			User entity = null;

			if (dto.getNameUser() == null || dto.getNameUser().isEmpty())
				throw new EmulatorException("Campo Mail es Obligacion", "6500");

			entity = userRepository.findByRut(dto.getRut());
			entity = entity == null ? userRepository.findByMail(dto.getMail()) : entity;
			if (entity == null) {
				entity = new User();
				entity.setPassword(util.SHA256(dto.getPass()));
				logger.info("Nuevo elemento");
			}

			entity.setRealm(dto.getRealm());
			entity.setNameUser(dto.getNameUser());
			entity.setAddress(dto.getAddress());
			entity.setAge(dto.getAge());
			entity.setCity(dto.getCity());
			entity.setFullName(dto.getFullName());
			entity.setMail(dto.getMail());
			entity.setMobile(dto.getMobile());
			entity.setRut(dto.getRut());
			entity.setType(EUserType.getTypeByName(dto.getType()));

			msg = UtilConst.SAVE_INFO_TO + new String(entity.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

			logger.info("Something went wrong: {}", msg);

			User userbd = userRepository.save(entity);
			response = new UserSaveResponse();
			((UserSaveResponse) response).setId(userbd.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EmulatorException("Error guardando usuario", "6500");
		}
		return response;
	}

}
