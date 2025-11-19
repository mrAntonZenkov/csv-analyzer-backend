package org.example.csvanalyzer.exception;

import org.example.csvanalyzer.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleProcessing(FileProcessingException ex) {
        return buildError("File processing error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedType(FileTypeNotSupportedException ex) {
        return buildError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ErrorResponseDto> handleTooLarge(FileTooLargeException ex) {
        return buildError(ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMultipartLimit(MaxUploadSizeExceededException ex) {
        return buildError("Uploaded file exceeds the maximum allowed size (50MB)",
                HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(RecordNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        return buildError("Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponseDto> buildError(String msg, HttpStatus status) {
        ErrorResponseDto dto = new ErrorResponseDto(
                msg,
                status.value(),
                Instant.now()
        );
        return new ResponseEntity<>(dto, status);
    }
}