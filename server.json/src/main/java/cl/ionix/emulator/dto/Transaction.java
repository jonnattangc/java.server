package cl.ionix.emulator.dto;

import lombok.Data;

@Data
public class Transaction {
	private String amount;
	private String datetime;
	private String timestamp;
	private String nsu;
	private String country;
	private String currency;
	private String rrn;
	private Merchant merchant;
}
