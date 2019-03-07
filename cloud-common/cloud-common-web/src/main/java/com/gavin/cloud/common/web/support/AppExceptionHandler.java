package com.gavin.cloud.common.web.support;

import com.gavin.cloud.common.base.exception.AbstractException;
import com.gavin.cloud.common.base.exception.CommonMessageType;
import com.gavin.cloud.common.base.exception.Exceptional;
import com.gavin.cloud.common.base.exception.ExceptionalBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807)
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 */
@RestControllerAdvice
public class AppExceptionHandler extends ExceptionalAdviceTrait {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<Exceptional> handleAppException(AbstractException ex, NativeWebRequest request) {
        return create(ex, ex, request);
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<Exceptional> handleValidationException(BindException ex, NativeWebRequest request) {
        return handleValidationException(ex, request, ex.getBindingResult());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Exceptional> handleValidationException(MethodArgumentNotValidException ex, NativeWebRequest request) {
        return handleValidationException(ex, request, ex.getBindingResult());
    }

    private ResponseEntity<Exceptional> handleValidationException(Throwable throwable, NativeWebRequest request, BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors().stream().map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage()).collect(Collectors.toList());
        Exceptional exceptional = ExceptionalBuilder.newInstance()
                .withStatus(CommonMessageType.ERR_VALIDATION.getStatus())
                .withTitle(CommonMessageType.ERR_VALIDATION.getPhrase())
                .with("fieldErrors", errors)
                .build();
        return create(exceptional, throwable, request);
    }

}
