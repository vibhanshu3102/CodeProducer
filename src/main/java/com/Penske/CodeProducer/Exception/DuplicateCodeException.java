package com.Penske.CodeProducer.Exception;

public class DuplicateCodeException extends RuntimeException {
    public DuplicateCodeException(String message) {
        super(message);
        System.out.println("yes");
    }
}