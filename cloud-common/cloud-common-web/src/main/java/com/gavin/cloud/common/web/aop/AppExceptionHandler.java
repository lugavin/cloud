package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.problem.AbstractException;
import com.gavin.cloud.common.base.problem.CommonMessageType;
import com.gavin.cloud.common.base.problem.Exceptional;
import com.gavin.cloud.common.base.problem.Problem;
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
public class AppExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<Problem> handleAppException(AbstractException ex, NativeWebRequest request) {
        return create(ex, ex, request);
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<Problem> handleValidationException(BindException ex, NativeWebRequest request) {
        return handleValidationException(ex, request, ex.getBindingResult());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidationException(MethodArgumentNotValidException ex, NativeWebRequest request) {
        return handleValidationException(ex, request, ex.getBindingResult());
    }

    private ResponseEntity<Problem> handleValidationException(Throwable throwable, NativeWebRequest request, BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        Exceptional exceptional = Problem.builder()
                .withStatus(CommonMessageType.ERR_VALIDATION.getStatus())
                .withTitle(CommonMessageType.ERR_VALIDATION.getPhrase())
                .with("fieldErrors", errors)
                .build();
        return create(throwable, exceptional, request);
    }

}
