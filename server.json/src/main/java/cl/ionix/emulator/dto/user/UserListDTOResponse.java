package cl.ionix.emulator.dto.user;

import java.util.ArrayList;
import java.util.List;

import cl.ionix.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class UserListDTOResponse implements IEmulator {
		
	private List<UserListDTOResponse.UserResponse> users = new ArrayList<>();
	
	@Data
	public static class UserResponse {
		private Long id;
		private String name;
		private String rut;
		private String mail;
		private String nameUser;
		private String city;
		private Integer age;
		private String address;       
		private String mobile;
		private String type;
	}
}
