package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.problem.DefaultProblem;
import com.gavin.cloud.common.base.problem.Exceptional;
import com.gavin.cloud.common.base.problem.ExceptionalBuilder;
import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
public abstract class AbstractExceptionHandler {

    protected ResponseEntity<Problem> create(final Throwable throwable,
                                             final Problem problem,
                                             final NativeWebRequest request) {
        return create(throwable, problem, request, new HttpHeaders());
    }

    protected ResponseEntity<Problem> create(final Throwable throwable,
                                             final Problem problem,
                                             final NativeWebRequest request,
                                             final HttpHeaders headers) {
        int statusCode = Optional.ofNullable(problem.getStatus())
                .orElse(Status.INTERNAL_SERVER_ERROR)
                .getStatusCode();

        final HttpStatus status = HttpStatus.valueOf(statusCode);
        if (status.is4xxClientError()) {
            log.warn("{}: {}", status.getReasonPhrase(), throwable.getMessage());
        } else if (status.is5xxServerError()) {
            log.error(status.getReasonPhrase(), throwable);
        }

        if (!(problem instanceof Exceptional)) {
            return new ResponseEntity<>(problem, headers, status);
        }

        ExceptionalBuilder builder = Problem.builder()
                .withStatus(problem.getStatus())
                .withTitle(problem.getTitle())
                .withDetail(problem.getDetail())
                .withCause(((Exceptional) problem).getCause())
                .with("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());
        problem.getParameters().forEach(builder::with);

        return new ResponseEntity<>(builder.build(), headers, status);
    }

}
