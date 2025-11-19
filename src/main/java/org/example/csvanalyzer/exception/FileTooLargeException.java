package org.example.csvanalyzer.exception;

public class FileTooLargeException extends RuntimeException {

    public FileTooLargeException(String msg) {
        super(msg);
    }
}