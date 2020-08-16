package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.exception.AppAlertException;
import com.gavin.cloud.common.base.exception.DefaultProblemType;
import com.gavin.cloud.common.base.exception.Problem;
import com.gavin.cloud.common.base.exception.ThrowableProblem;
import com.gavin.cloud.common.web.util.HeaderUtils;
import org.springframework.http.ResponseEntity;
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
public class ExceptionTranslator implements ProblemAdviceTrait {

    @ExceptionHandler(AppAlertException.class)
    public ResponseEntity<Problem> handleException(AppAlertException ex, NativeWebRequest request) {
        return create(ex, request, HeaderUtils.createFailureAlert(ex.getAlertType().getEntityName(), ex.getAlertType().getErrorKey(), ex.getMessage()));
    }

    @ExceptionHandler(ThrowableProblem.class)
    public ResponseEntity<Problem> handleException(ThrowableProblem problem, NativeWebRequest request) {
        return create(problem, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleException(MethodArgumentNotValidException ex, NativeWebRequest request) {
        return handleException(ex, request, ex.getBindingResult());
    }

    private ResponseEntity<Problem> handleException(Throwable throwable, NativeWebRequest request, BindingResult bindingResult) {
        List<String> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.toList());
        // String errors = String.join(", ", fieldErrors);
        DefaultProblemType problemType = DefaultProblemType.CONSTRAINT_VIOLATION_TYPE;
        ThrowableProblem problem = Problem.builder()
                .withType(problemType.getType())
                .withStatus(problemType.getStatus())
                .withTitle(problemType.getTitle())
                .with("message", "error.validation")
                .with("fieldErrors", fieldErrors)
                .build();
        return create(throwable, problem, request);
    }

}
