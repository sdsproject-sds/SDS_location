package org.sds.sdslocation.exeption;


import com.sds.integration.commons.model.AbstractBaseApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.sds.sdslocation.controller.ApiResponse;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AbstractBaseApiResponse<?>> handleValidationExceptions(Exception e) {
        var errorId = UUID.randomUUID().toString();
        log.error("Exception occurred, Error Id: {} Error: {}", errorId, e.getMessage(), e);
        String errors = e.getMessage();
        return ResponseEntity.status(500).body(new ApiResponse<>().error("500",
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
                e.getMessage(),
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
    }
}
