package com.handloom.marketplace.exception;

/** Expected, user-facing problem (out of stock, empty cart...). Shown as a red alert. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
