package cl.jonnattan.emulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import cl.jonnattan.emulator.dto.ints.IEmulator;
import lombok.Data;

@Data
public class GetPaymentMethodInfoResponseDTO implements IEmulator {
	private GetPaymentMethodInfoResponseDTO.InfoData data;
	private GetPaymentMethodInfoResponseDTO.InfoMeta meta;

	@Data
	public static class InfoData {

		private Boolean activated;
		@JsonProperty("available_balance")
		private Double availableBalance;
		@JsonProperty("beneficiary_rut")
		private String beneficiaryRut;
		private Long bordero;
		@JsonProperty("card_number")
		private String cardNumber;
		@JsonProperty("contract_identification")
		private Long contractIdentification;
		@JsonProperty("has_observation")
		private Boolean hasObservation;
		@JsonProperty("is_personalized")
		private Boolean isPersonalized;
		@JsonProperty("is_reissue")
		private Boolean isReissue;
		@JsonProperty("lost_card")
		private Boolean lostCard;
		@JsonProperty("masked_card_number")
		private String maskedCardNumber;
		@JsonProperty("observation_message")
		private String observationMessage;
		@JsonProperty("risk_condition")
		private Boolean riskCondition;
		@JsonProperty("tokenized_card_number")
		private String tokenizedCardNumber;
	}

	@Data
	public static class InfoMeta {

		private String[] messages;
		private String status;
	}
}
