package cl.ionix.emulator.dto;

import lombok.Data;

@Data
public class EdrPaymentCreateCryptogramRequestDTO {
	RequestorInfo requestorInfo;
	
	@Data
    public static class RequestorInfo {
        String rid;
    }
}
