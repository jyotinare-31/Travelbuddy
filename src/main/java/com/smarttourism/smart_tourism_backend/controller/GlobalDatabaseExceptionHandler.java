package com.smarttourism.smart_tourism_backend.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalDatabaseExceptionHandler {

    @ExceptionHandler({
        CannotCreateTransactionException.class,
        JDBCConnectionException.class,
        DataAccessException.class,
        SQLException.class
    })
    public ResponseEntity<Map<String, Object>> handleDatabaseException(Exception ex) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        errorDetails.put("error", "Database Connection Failure");
        errorDetails.put("message", "Unable to establish connection with PostgreSQL database. Please ensure PostgreSQL is running on port 5432 and credentials are correct.");
        errorDetails.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorDetails);
    }
}

