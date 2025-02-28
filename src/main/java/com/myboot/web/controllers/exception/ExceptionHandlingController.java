package com.myboot.web.controllers.exception;

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
    public ResponseEntity<String> catchObjectOptimisticLockingFailureException(
            HttpServletRequest httpServletRequest, ObjectOptimisticLockingFailureException e) {
        logger.error("ObjectOptimisticLockingFailureException!!!! {}", e.getMessage());
        return new ResponseEntity<>(Arrays.stream(e.getLocalizedMessage().split(":")).findFirst().orElseThrow(), HttpStatus.BAD_REQUEST);
    }
}
