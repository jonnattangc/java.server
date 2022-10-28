package cl.ionix.emulator.dto;

import lombok.Data;

@Data
public class CommerceMailRequestDTO {
	private CommerceMailRequestDTO.SecurityDTO security;
	private CommerceMailRequestDTO.NotificationDTO notification;

	@Data
	public static class SecurityDTO {
		private String partner_identifier;
		private String key;
	}

	@Data
	public static class NotificationDTO {
		private CommerceMailRequestDTO.NotificationBaseDTO notification_base;
		private Integer product_type;
		private String branch_identifier;
		private String to;
		private String cc;
		private String subject;
		private String attachments;
	}

	@Data
	public static class NotificationBaseDTO {
		private String data;
		private String rut;
		private String application_identifier;
		private String type_identifier;
	}

}
