package cl.ionix.emulator.dto.user;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String rut;	
	private String mail;
	private String nameUser;
	private String fullName;
	private String address;
	private String mobile;
	private String type;	
	private Integer age;
	private String city;
	private String pass;
	private String realm;
	
}
