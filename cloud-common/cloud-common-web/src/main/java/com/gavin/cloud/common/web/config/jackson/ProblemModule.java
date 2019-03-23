package com.gavin.cloud.common.web.config.jackson;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gavin.cloud.common.base.problem.Exceptional;
import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.Status;
import com.gavin.cloud.common.base.problem.StatusType;
import com.gavin.cloud.common.base.problem.ThrowableProblem;
import com.gavin.cloud.common.base.problem.UnknownStatus;

import java.io.IOException;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public final class ProblemModule extends Module {

    private final boolean stackTraces;

    public ProblemModule() {
        this(Boolean.FALSE);
    }

    public ProblemModule(boolean stackTraces) {
        this.stackTraces = stackTraces;
    }

    @Override
    public String getModuleName() {
        return ProblemModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return VersionUtil.versionFor(ProblemModule.class);
    }

    @Override
    public void setupModule(final SetupContext context) {
        final SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(Exceptional.class, stackTraces ?
                ExceptionalWithStacktraceMixin.class :
                ExceptionalMixin.class);
        module.setMixInAnnotation(Problem.class, ProblemMixIn.class);
        module.addSerializer(StatusType.class, new StatusTypeJsonSerializer());
        module.addDeserializer(StatusType.class, new StatusTypeJsonDeserializer());
        module.setupModule(context);
    }

    private static class StatusTypeJsonSerializer extends JsonSerializer<StatusType> {
        @Override
        public void serialize(StatusType status, JsonGenerator json, SerializerProvider serializers) throws IOException {
            json.writeNumber(status.getStatusCode());
        }
    }

    private static class StatusTypeJsonDeserializer extends JsonDeserializer<StatusType> {
        @Override
        public StatusType deserialize(final JsonParser json, final DeserializationContext context) throws IOException {
            final int statusCode = json.getIntValue();
            StatusType status = Status.fromStatusCode(statusCode);
            return status == null ? new UnknownStatus(statusCode) : status;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface ExceptionalMixin {

        @JsonIgnore
        String getMessage();

        @JsonIgnore
        String getLocalizedMessage();

        @JsonInclude(NON_NULL)
        ThrowableProblem getCause();

        @JsonIgnore
        StackTraceElement[] getStackTrace();

        @JsonIgnore
        Throwable[] getSuppressed();

    }

    private interface ExceptionalWithStacktraceMixin extends ExceptionalMixin {

        @Override
        @JsonProperty("stacktrace")
        @JsonSerialize(contentUsing = ToStringSerializer.class)
        StackTraceElement[] getStackTrace();

    }

    private interface ProblemMixIn extends Problem {

        @Override
        @JsonAnyGetter
        Map<String, Object> getParameters();

    }

}