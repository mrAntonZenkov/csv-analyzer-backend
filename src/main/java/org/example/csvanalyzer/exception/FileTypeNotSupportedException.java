package org.example.csvanalyzer.exception;

public class FileTypeNotSupportedException extends RuntimeException {

    public FileTypeNotSupportedException(String msg) {
        super(msg);
    }
}