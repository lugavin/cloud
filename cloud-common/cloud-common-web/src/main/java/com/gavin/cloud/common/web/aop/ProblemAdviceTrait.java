package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.ProblemBuilder;
import com.gavin.cloud.common.base.problem.ThrowableProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

import static com.gavin.cloud.common.base.problem.AlertType.ALERT_KEY_MESSAGE;
import static com.gavin.cloud.common.base.problem.HttpStatus.INTERNAL_SERVER_ERROR;

public interface ProblemAdviceTrait {

    Logger LOG = LoggerFactory.getLogger(ProblemAdviceTrait.class);

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
        int statusCode = Optional.ofNullable(problem.getStatus()).orElse(INTERNAL_SERVER_ERROR).getStatusCode();
        HttpStatus status = HttpStatus.valueOf(statusCode);
        if (status.is4xxClientError()) {
            LOG.warn("{}: {}", status.getReasonPhrase(), throwable.getMessage());
        } else if (status.is5xxServerError()) {
            LOG.error(status.getReasonPhrase(), throwable);
        }

        if (!(problem instanceof ThrowableProblem)) {
            return new ResponseEntity<>(problem, headers, status);
        }
        ProblemBuilder builder = Problem.builder()
                .withType(problem.getType())
                .withStatus(problem.getStatus())
                .withTitle(problem.getTitle())
                .withDetail(problem.getDetail())
                .withCause(((ThrowableProblem) problem).getCause())
                .with("path", ((HttpServletRequest) request.getNativeRequest()).getRequestURI())
                .with("timestamp", System.currentTimeMillis());
        problem.getParameters().forEach(builder::with);
        if (!problem.getParameters().containsKey(ALERT_KEY_MESSAGE) && Objects.nonNull(problem.getStatus())) {
            builder.with(ALERT_KEY_MESSAGE, "error.http." + problem.getStatus().getStatusCode());
        }
        return new ResponseEntity<>(builder.build(), headers, status);
    }

}
