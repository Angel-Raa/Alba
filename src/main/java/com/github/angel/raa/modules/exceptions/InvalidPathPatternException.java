package com.github.angel.raa.modules.exceptions;

public class InvalidPathPatternException extends RuntimeException {
    private String path;
    private String message;
    public InvalidPathPatternException() {
        super("Invalid path pattern");
    }
    public InvalidPathPatternException(String message) {
        super(message);
    }

    public InvalidPathPatternException(String message, String path) {
        super(message);
        this.path = path;
    }

    public InvalidPathPatternException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPathPatternException(Throwable cause) {
        super(cause);
    }

    public  String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

}
