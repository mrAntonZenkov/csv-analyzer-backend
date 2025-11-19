package org.example.csvanalyzer.exception;

public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String msg) {
        super(msg);
    }
}