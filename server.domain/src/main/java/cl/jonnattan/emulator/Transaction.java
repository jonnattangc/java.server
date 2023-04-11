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

import cl.jonnattan.emulator.enums.TransactionStatus;
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
