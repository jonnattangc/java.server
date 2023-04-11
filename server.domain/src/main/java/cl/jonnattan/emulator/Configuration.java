package cl.jonnattan.emulator;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import cl.jonnattan.emulator.enums.TypeResponse;
import lombok.Data;

/**
 * Clase tupla de la base de datos para configuration
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
@Entity
@Table(name = "configuration")
@Data
public class Configuration {
	@Id
	@Column(name = "id_configuration", nullable = false, unique = true)
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

	@Column(name = "endpoint", nullable = false)
	private String endpoint;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private TypeResponse type;

	@Column(name = "error", nullable = false)
	Boolean error;

	@Column(name = "code")
	String code;

	@Column(name = "message")
	private String message;

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
