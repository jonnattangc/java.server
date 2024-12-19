package cl.jonnattan.emulator;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import cl.jonnattan.emulator.enums.TransactionStatus;
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
import lombok.Data;

/**
 * Clase tupla de la base de datos para transaciones
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
@Entity
@Table(name = "transaction")
@Data
public class Transaction {
	@Id
	@Column(name = "id_transaction", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "request",nullable = false, columnDefinition = "TEXT")
	private String request;

	@Column(name = "json_id", nullable = false, columnDefinition = "TEXT")
	private String jsonId;
	
	@Column(name = "data", nullable = false, columnDefinition = "TEXT")
	private String data;

	@Column(name = "authorization_id")
	private String authorizationId;
	
	@Column(name = "card")
	private String card;
	
	@Column(name = "amount")
	private String amount;

	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
	private Date updatedAt;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
	private Date createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    TransactionStatus status;
	
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
