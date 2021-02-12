package me.untoldstories.be.error.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessagePerFieldException extends RuntimeException{
    private final Map<String, Object> errorMap = new HashMap<>();

    public void addError(String fieldName, String errorDetails) {
        errorMap.put(fieldName, errorDetails);
    }

    public Map<String, Object> getErrorMap() {
        return errorMap;
    }
}
