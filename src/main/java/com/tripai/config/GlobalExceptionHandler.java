package com.tripai.config;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> bad(IllegalArgumentException e) { return err(HttpStatus.BAD_REQUEST, e.getMessage()); }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,String>> creds(BadCredentialsException e) { return err(HttpStatus.UNAUTHORIZED, "Invalid email or password"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> valid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).findFirst().orElse("Validation error");
        return err(HttpStatus.BAD_REQUEST, msg);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> runtime(RuntimeException e) { return err(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); }
    private ResponseEntity<Map<String,String>> err(HttpStatus s, String m) { return ResponseEntity.status(s).body(Map.of("error",m)); }
}
