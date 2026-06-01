package org.sds.sdslocation.exeption;


import com.sds.integration.commons.model.AbstractBaseApiResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sds.sdslocation.controller.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

/**
 * @author Joseph.Kibe. Created On 03 May 2026 21:34
 */

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AbstractBaseApiResponse<String>> handleValidationExceptions(Exception e) {
        var errorId = UUID.randomUUID().toString();
        log.error("Exception occurred, Error Id: {} Error: {}", errorId, e.getMessage(), e);
        String errors = e.getMessage();
        return ResponseEntity.status(500).body(new ApiResponse<String>().error("500",
                "Internal Server Error",
                errors,
                errorId
        ));
    }


    @ExceptionHandler(SdsLocationException.class)
    public ResponseEntity<AbstractBaseApiResponse<Void>> handleValidationExceptions(SdsLocationException e) {
        var errorId = UUID.randomUUID().toString();
        log.error("SdsLocationException occurred, Error Id: {} Error: {}", errorId, e.getMessage(), e);

        return ResponseEntity.status(400).body(new ApiResponse<Void>().error(
                "400",
                "Request Failed",
                "Error occurred while processing the request",
                errorId
        ));
    }

    @ExceptionHandler(SdsLocationNotFoundException.class)
    public ResponseEntity<AbstractBaseApiResponse<Void>> handleVNotFoundExceptions(SdsLocationNotFoundException e) {
        var errorId = UUID.randomUUID().toString();
        log.error("SdsLocationNotFoundException occurred, Error Id: {} Error: {}", errorId, e.getMessage(), e);

        return ResponseEntity.status(404).body(new ApiResponse<Void>().error(
                "404",
                "Request Failed",
                e.getMessage(),
                errorId
        ));
    }@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AbstractBaseApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException e) {
        var errorId = UUID.randomUUID().toString();
        List<String> errors = e.getBindingResult().getFieldErrors().stream().map(
                fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).toList();
        log.error("SdsLocationNotFoundException occurred, Error Id: {} Error: {}", errorId, e.getMessage(), e);

        return ResponseEntity.status(404).body(new ApiResponse<Void>().error(
                "404",
                "Request Failed",
                errors.toString(),
                errorId
        ));
    }
}
