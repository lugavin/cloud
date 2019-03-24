package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.problem.BadRequestAlertException;
import com.gavin.cloud.common.base.problem.Exceptional;
import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.ProblemType;
import com.gavin.cloud.common.base.problem.Status;
import com.gavin.cloud.common.base.problem.ThrowableProblem;
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

    @ExceptionHandler(ThrowableProblem.class)
    public ResponseEntity<Problem> handleProblem(ThrowableProblem problem, NativeWebRequest request) {
        return create(problem, request);
    }

    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<Problem> handleBadRequestAlertException(BadRequestAlertException ex, NativeWebRequest request) {
        return create(ex, request, HeaderUtils.createFailureAlert(ex.getEntityName(), ex.getErrorKey(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, NativeWebRequest request) {
        return handleValidationException(ex, request, ex.getBindingResult());
    }

    private ResponseEntity<Problem> handleValidationException(Throwable throwable, NativeWebRequest request, BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(f -> f.getField() + ":" + f.getDefaultMessage())
                .collect(Collectors.toList());
        Exceptional exceptional = Problem.builder()
                .withType(ProblemType.CONSTRAINT_VIOLATION_TYPE)
                .withTitle("Method argument not valid")
                .withStatus(Status.BAD_REQUEST)
                .with("message", "error.validation")
                .with("fieldErrors", errors)
                .build();
        return create(throwable, exceptional, request);
    }

}
