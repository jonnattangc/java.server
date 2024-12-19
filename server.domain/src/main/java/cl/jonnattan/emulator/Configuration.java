package cl.jonnattan.emulator;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
