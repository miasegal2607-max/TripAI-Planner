package com.tripai.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class ChatRequest {
    @NotNull private Long tripId;
    @NotBlank private String message;
}
