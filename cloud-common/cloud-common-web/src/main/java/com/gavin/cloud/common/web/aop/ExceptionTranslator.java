package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.exception.DefaultProblemType;
import com.gavin.cloud.common.base.exception.Problem;
import com.gavin.cloud.common.base.exception.ThrowableProblem;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807)
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 */
@RestControllerAdvice
public class ExceptionTranslator implements ProblemAdviceTrait {

    @ExceptionHandler(ThrowableProblem.class)
    public ResponseEntity<Problem> handleProblem(ThrowableProblem problem, NativeWebRequest request) {
        return create(problem, request);
    }

    // @ExceptionHandler(BadRequestAlertException.class)
    // public ResponseEntity<Problem> handleBadRequestAlertException(BadRequestAlertException ex, NativeWebRequest request) {
    //     return create(ex, request, HeaderUtils.createFailureAlert(ex.getEntityName(), ex.getErrorKey(), ex.getMessage()));
    // }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, NativeWebRequest request) {
        return handleValidationException(ex, request, ex.getBindingResult());
    }

    private ResponseEntity<Problem> handleValidationException(Throwable throwable, NativeWebRequest request, BindingResult bindingResult) {
        String fieldErrors = bindingResult.getFieldErrors().stream()
                .map(f -> f.getField() + ":" + f.getDefaultMessage())
                .reduce((s1, s2) -> s1 + s2)
                .orElse("");
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
