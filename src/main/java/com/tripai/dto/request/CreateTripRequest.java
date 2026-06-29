package com.tripai.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
@Data public class CreateTripRequest {
    @NotBlank private String destination;
    @NotNull @Min(1) @Max(30) private Integer numDays;
    private LocalDate startDate;
    private String budget;
    private String style;
    private List<String> interests;
}
