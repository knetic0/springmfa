package com.mehmetsolak.springmfa.presentation.api;

import com.mehmetsolak.springmfa.application.exceptions.InvalidCredentialsException;
import com.mehmetsolak.springmfa.application.exceptions.InvalidMfaCodeException;
import com.mehmetsolak.springmfa.application.exceptions.UserNotFoundException;
import com.mehmetsolak.springmfa.application.features.register.EmailAlreadyExists;
import com.mehmetsolak.springmfa.domain.shared.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidMfaCodeException.class)
    ProblemDetail handleInvalidMfaCode(InvalidMfaCodeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    ProblemDetail handleEmailExists(EmailAlreadyExists ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
