package cl.jonnattan.emulator;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

/**
 * Clase tupla de la base de datos para tarjetas
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR
 * @version 1.0 del 22-06-2020
 * 
 */
@Entity
@Table(name = "card")
@Data
public class Card {
	@Id
	@Column(name = "id_card", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "request", nullable = false, columnDefinition = "TEXT")
	private String request;

	@Column(name = "token")
	private String token;
	
	@Column(name = "profile", nullable = false, columnDefinition = "TEXT")
	private String profile;
	
	@Column(name = "r_id")
	private Long rId;
	
	@Column(name = "client")
	private String client;
	
	@Column(name = "card_number")
	private String cardNumber;
	
	@Column(name = "amount")
	private Long amount;
	
	@Column(name = "rut")
	private String rut;
	
	@Column(name = "pin")
	private String pin;

	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
	private Date updatedAt;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
	private Date createdAt;

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
