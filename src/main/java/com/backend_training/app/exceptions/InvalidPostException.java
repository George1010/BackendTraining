package com.backend_training.app.exceptions;

import com.backend_training.app.dto.ErrorDetail;
import java.util.List;

public class InvalidPostException extends RuntimeException {
    private List<ErrorDetail> errorDetails;

    public InvalidPostException(List<ErrorDetail> errorDetails) {
        super("Invalid request data. Please review the request and try again.");
        this.errorDetails = errorDetails;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }
}
