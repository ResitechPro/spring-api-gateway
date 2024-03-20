package com.resitechpro.exception;


import com.resitechpro.exception.customexceptions.BadRequestException;
import com.resitechpro.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
        BadRequestException.class,
        RuntimeException.class,
        Exception.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<String>> abstractBadRequest(Exception ex) {
        Response<String> response = new Response<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }
}
