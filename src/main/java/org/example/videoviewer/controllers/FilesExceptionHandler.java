package org.example.videoviewer.controllers;

import org.example.videoviewer.exceptions.BaseException;
import org.example.videoviewer.exceptions.FileExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FilesExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(final BaseException ex) {
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        return ResponseEntity.status(responseStatus.value()).body(ex.getMessage());
    }
}
