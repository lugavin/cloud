package com.gavin.cloud.common.web.aop;

import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.exception.Exceptional;
import com.gavin.cloud.common.base.exception.ExceptionalBuilder;
import com.gavin.cloud.common.base.exception.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
public abstract class ExceptionalAdviceTrait {

    protected ResponseEntity<Exceptional> create(final Exceptional exceptional,
                                                 final Throwable throwable,
                                                 final NativeWebRequest request) {
        return create(exceptional, throwable, request, new HttpHeaders());
    }

    protected ResponseEntity<Exceptional> create(final Exceptional exceptional,
                                                 final Throwable throwable,
                                                 final NativeWebRequest request,
                                                 final HttpHeaders headers) {
        int statusCode = Optional.ofNullable(exceptional.getStatus())
                .orElse(Status.INTERNAL_SERVER_ERROR)
                .getStatusCode();
        final HttpStatus status = HttpStatus.valueOf(statusCode);

        if (status.is4xxClientError()) {
            log.warn("{}: {}", status.getReasonPhrase(), throwable.getMessage());
        } else if (status.is5xxServerError()) {
            log.error(status.getReasonPhrase(), throwable);
        }

        if (!(exceptional instanceof AppException)) {
            return new ResponseEntity<>(exceptional, headers, status);
        }

        ExceptionalBuilder builder = ExceptionalBuilder.newInstance()
                .withStatus(exceptional.getStatus())
                .withTitle(exceptional.getTitle())
                .withDetail(exceptional.getDetail())
                .withCause(((AppException) exceptional).getCause())
                .with("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        exceptional.getParameters().forEach(builder::with);
        //if (!exceptional.getParameters().containsKey("message") && exceptional.getStatus() != null) {
        //    builder.with("message", "error.http." + exceptional.getStatus().getStatusCode());
        //}

        return new ResponseEntity<>(builder.build(), headers, status);
    }

}
