package me.untoldstories.be.error;

import me.untoldstories.be.constants.ErrorResponseCode;
import me.untoldstories.be.error.exceptions.ErrorMessagePerFieldException;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final static String BAD_REQUEST_ERROR_RESPONSE_CODE_LABEL = "id";

    //instead of creating a new SingleMessageResponse at each request, the response is cached.
    private final SingleMessageResponse methodNotSupportedErrorResponse =  new SingleMessageResponse("Method Not Allowed");
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotSupportedError(HttpServletRequest request, Exception exception) {
        return methodNotSupportedErrorResponse;
    }

    private final SingleMessageResponse pathVariableTypeMismatchErrorResponse = new SingleMessageResponse("Path Variable Type Mismatch");
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handlePathVariableTypeMismatch(HttpServletRequest request, Exception exception) {
        return pathVariableTypeMismatchErrorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleRequestParameterMissing(HttpServletRequest request, Exception exception) {
        return new SingleMessageResponse(exception.getMessage());
    }

    private final SingleMessageResponse messageNotReadableErrorResponse = new SingleMessageResponse("Invalid request body");
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleBadRequestBodyError(HttpServletRequest request, Exception exception) {
        return messageNotReadableErrorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleErrorMessagePerFieldGeneratedByValidator(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        errors.put(BAD_REQUEST_ERROR_RESPONSE_CODE_LABEL, ErrorResponseCode.ERROR_MESSAGE_PER_FIELD);
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ErrorMessagePerFieldException.class)
    public Object handleErrorMessagePerFieldException(HttpServletRequest request, Exception exception) {
        ErrorMessagePerFieldException validationException = (ErrorMessagePerFieldException) exception;
        Map<String, Object> errors = validationException.getErrorMap();

        errors.put(BAD_REQUEST_ERROR_RESPONSE_CODE_LABEL, ErrorResponseCode.ERROR_MESSAGE_PER_FIELD);

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SingleErrorMessageException.class)
    public Object handleSingleErrorMessageException(HttpServletRequest request, Exception exception) {
        SingleErrorMessageException validationException = (SingleErrorMessageException) exception;
        Map<String, Object> errors = new HashMap<>();

        errors.put(BAD_REQUEST_ERROR_RESPONSE_CODE_LABEL, ErrorResponseCode.SINGLE_ERROR_MESSAGE);
        errors.put("msg", validationException.getMessage());

        return errors;
    }

    private final SingleMessageResponse internalServerErrorResponse = new SingleMessageResponse("Internal Server Error");
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Object handleInternalServerError(HttpServletRequest request, Exception exception) {
        //todo: send an email
        String requestInfo = new StringBuilder("Server Error:: ").append(request.getMethod()).append(" ").append(request.getRequestURL()).toString();
        logger.error(requestInfo, exception);

        return internalServerErrorResponse;
    }
}