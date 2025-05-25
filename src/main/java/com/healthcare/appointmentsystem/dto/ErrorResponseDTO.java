package com.healthcare.appointmentsystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> validationErrors;
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> details = new ArrayList<>();
    
    public void addValidationError(String field, String message) {
        if (validationErrors == null) {
            return;
        }
        validationErrors.put(field, message);
    }
    
    public void addDetail(String detail) {
        details.add(detail);
    }
}
