package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.ProblemBuilder;
import com.gavin.cloud.common.base.problem.ProblemType;
import com.gavin.cloud.common.base.problem.Status;
import com.gavin.cloud.common.base.problem.ThrowableProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface AdviceTrait {

    Logger LOG = LoggerFactory.getLogger(AdviceTrait.class);

    default ResponseEntity<Problem> create(final ThrowableProblem problem,
                                           final NativeWebRequest request) {
        return create(problem, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final ThrowableProblem problem,
                                           final NativeWebRequest request,
                                           final HttpHeaders headers) {
        return create(problem, problem, request, headers);
    }

    default ResponseEntity<Problem> create(final Throwable throwable,
                                           final Problem problem,
                                           final NativeWebRequest request) {
        return create(throwable, problem, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final Throwable throwable,
                                           final Problem problem,
                                           final NativeWebRequest request,
                                           final HttpHeaders headers) {
        int statusCode = Optional.ofNullable(problem.getStatus())
                .orElse(Status.INTERNAL_SERVER_ERROR)
                .getStatusCode();

        final HttpStatus status = HttpStatus.valueOf(statusCode);
        if (status.is4xxClientError()) {
            LOG.warn("{}: {}", status.getReasonPhrase(), throwable.getMessage());
        } else if (status.is5xxServerError()) {
            LOG.error(status.getReasonPhrase(), throwable);
        }

        if (!(problem instanceof ThrowableProblem)) {
            return new ResponseEntity<>(problem, headers, status);
        }

        ProblemBuilder builder = Problem.builder()
                .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ProblemType.DEFAULT_TYPE : problem.getType())
                .withTitle(problem.getTitle())
                .withStatus(problem.getStatus())
                .withDetail(problem.getDetail())
                .withInstance(problem.getInstance())
                .withCause(((ThrowableProblem) problem).getCause())
                .with("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());
        problem.getParameters().forEach(builder::with);
        if (!problem.getParameters().containsKey("message") && problem.getStatus() != null) {
            builder.with("message", "error.http." + problem.getStatus().getStatusCode());
        }
        return new ResponseEntity<>(builder.build(), headers, status);
    }

}
