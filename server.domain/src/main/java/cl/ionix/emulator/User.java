package cl.ionix.emulator;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * Clase tupla de la base de datos para usuarios
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR APITEC
 * @version 1.0 del 22-06-2020
 * 
 */
@Entity
@Table(name = "user")
@Data
public class User {
	@Id
	@Column(name = "id_user", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
	private Date updatedAt;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
	private Date createdAt;
	
	@Column(name = "name_user")
	private String nameUser;

	@Column(name = "password")
	private String password;
	
	@Column(name = "access_token")
	private String accessToken;
	
	@Column(name = "realm")
	private String realm;
	
	@PrePersist
	public void prePersist() {
		createdAt = new Date();
		updatedAt = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = new Date();
	}
}
