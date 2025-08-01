package com.myboot.web.controllers.exception;

import com.myboot.exceptions.CustomUserIsCreatedException;
import com.myboot.exceptions.CustomUserNotFoundException;
import com.myboot.response.ErrorBody;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class ExceptionHandlingController {
    private Logger logger = LogManager.getLogger(ExceptionHandlingController.class);

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorBody> catchObjectOptimisticLockingFailureException(
            HttpServletRequest httpServletRequest, ObjectOptimisticLockingFailureException e) {
        logger.error("ObjectOptimisticLockingFailureException! {}", e.getMessage());
        return new ResponseEntity<>(
                ErrorBody.builder().
                        errorClass(ObjectOptimisticLockingFailureException.class.getSimpleName()).
                        errorMessage(Arrays.stream(e.getLocalizedMessage().split(":")).
                                findFirst().orElseThrow()). build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( CustomUserNotFoundException.class)
    public ResponseEntity<ErrorBody> catchDataIntegrityViolationException(
            HttpServletRequest httpServletRequest, CustomUserNotFoundException e) {
        logger.error("CustomUserNotFoundException! {}", e.getMessage());
        return new ResponseEntity<>(
                ErrorBody.builder().
                        errorClass(CustomUserNotFoundException.class.getSimpleName()).
                        errorMessage(Arrays.stream(e.getLocalizedMessage().split(":")).
                                findFirst().orElseThrow()). build(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler( CustomUserIsCreatedException.class)
    public ResponseEntity<ErrorBody> catchDataIntegrityViolationException(
            HttpServletRequest httpServletRequest, CustomUserIsCreatedException e) {
        logger.error("CustomUserIsCreatedException! {}", e.getMessage());
        return new ResponseEntity<>(
                ErrorBody.builder().
                        errorClass(CustomUserIsCreatedException.class.getSimpleName()).
                        errorMessage(Arrays.stream(e.getLocalizedMessage().split(":")).
                                findFirst().orElseThrow()). build(), HttpStatus.BAD_REQUEST);
    }
}
