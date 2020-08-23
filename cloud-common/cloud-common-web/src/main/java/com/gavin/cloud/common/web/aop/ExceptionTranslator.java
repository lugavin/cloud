package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.problem.AppAlertException;
import com.gavin.cloud.common.base.problem.AppBizException;
import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.ThrowableProblem;
import com.gavin.cloud.common.web.util.HeaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.stream.Collectors;

import static com.gavin.cloud.common.base.problem.DefaultProblemType.CONSTRAINT_VIOLATION_TYPE;

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

    /**
     * Form request parameter verification failure will throw {@link BindException}
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Problem> handleException(BindException ex, NativeWebRequest request) {
        return handleException(ex, ex.getBindingResult(), request);
    }

    /**
     * JSON request parameter verification failure will throw {@link MethodArgumentNotValidException}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleException(MethodArgumentNotValidException ex, NativeWebRequest request) {
        return handleException(ex, ex.getBindingResult(), request);
    }

    private ResponseEntity<Problem> handleException(Throwable throwable, BindingResult bindingResult, NativeWebRequest request) {
        return create(throwable, new AppBizException(CONSTRAINT_VIOLATION_TYPE, bindingResult.getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining(", "))), request);
    }

}
