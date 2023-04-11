package cl.jonnattan.emulator.dto;

import lombok.Data;

@Data
public class TransactionVoid {
	private String purchaseTransactionId;
	private String amount;
	private String datetime;
	private String timestamp;
	private String nsu;
	private String country;
	private String currency;
	private String rrn;
	private Merchant merchant;
	private Authorization authorization;
}
