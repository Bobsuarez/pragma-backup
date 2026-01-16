package co.com.pragma.model.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private Object body;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message,Object body) {
        super(message);
        this.body = body;
    }
}

