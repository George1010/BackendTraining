package com.backend_training.app.exceptions;

import com.backend_training.app.dto.ErrorDetail;
import com.backend_training.app.dto.PostResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPostException.class)
    public ResponseEntity<PostResponse> handleInvalidPostException(InvalidPostException ex) {
        List<ErrorDetail> errorDetails = ex.getErrorDetails();
        PostResponse errorResponse = new PostResponse("Invalid request data. Please review the request and try again.", errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<PostResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        errorDetails.add(new ErrorDetail(null, ex.getMessage(), "not_found"));

        PostResponse errorResponse = new PostResponse("Resource not found.", errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<PostResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        errorDetails.add(new ErrorDetail(null, ex.getMessage(), "too_many_requests"));

        PostResponse errorResponse = new PostResponse("Rate limit exceeded.", errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({Exception.class, InternalServerException.class})
    public ResponseEntity<PostResponse> handleGenericException(Exception ex) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        errorDetails.add(new ErrorDetail(null, "An unexpected error occurred", "internal_error"));

        PostResponse errorResponse = new PostResponse("Internal Server Error", errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}