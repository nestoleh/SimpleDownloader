package com.nestoleh.simpledownloader.exceptions;

/**
 * File download exception
 * @author oleg.nestyuk
 */
public class FileDownloadException extends Exception {
    public FileDownloadException() {
    }

    public FileDownloadException(String message) {
        super(message);
    }

    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileDownloadException(Throwable cause) {
        super(cause);
    }
}
