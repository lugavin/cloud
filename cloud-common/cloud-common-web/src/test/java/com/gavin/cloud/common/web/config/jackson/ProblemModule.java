package com.gavin.cloud.common.web.config.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gavin.cloud.common.web.problem.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public final class ProblemModule extends com.fasterxml.jackson.databind.Module {

    private final boolean stackTraces;
    private final Map<Integer, StatusType> statuses;

    public ProblemModule() {
        this(Boolean.FALSE, new Class[]{Status.class});
    }

    public <E extends Enum & StatusType> ProblemModule(boolean stackTraces, Class<? extends E>[] types) {
        this(stackTraces, buildIndex(types));
    }

    private ProblemModule(boolean stackTraces, Map<Integer, StatusType> statuses) {
        this.stackTraces = stackTraces;
        this.statuses = statuses;
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

        module.addSerializer(StatusType.class, new StatusTypeSerializer());
        module.addDeserializer(StatusType.class, new StatusTypeDeserializer(statuses));

        module.setupModule(context);
    }

    private static <E extends Enum & StatusType> Map<Integer, StatusType> buildIndex(Class<? extends E>[] types) {
        final Map<Integer, StatusType> index = new HashMap<>();
        for (Class<? extends E> type : types) {
            for (E status : type.getEnumConstants()) {
                if (index.containsKey(status.getStatusCode())) {
                    throw new IllegalArgumentException("Duplicate status codes are not allowed");
                }
                index.put(status.getStatusCode(), status);
            }
        }
        return index;
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

    @JsonInclude(NON_EMPTY)
    private interface ProblemMixIn extends Problem {

        @Override
        @JsonAnyGetter
        Map<String, Object> getParameters();

    }

    private final class StatusTypeSerializer extends JsonSerializer<StatusType> {

        @Override
        public void serialize(final StatusType status, final JsonGenerator json, final SerializerProvider serializers) throws IOException {
            json.writeNumber(status.getStatusCode());
        }

    }

    private final class StatusTypeDeserializer extends JsonDeserializer<StatusType> {

        private final Map<Integer, StatusType> index;

        StatusTypeDeserializer(final Map<Integer, StatusType> index) {
            this.index = index;
        }

        @Override
        public StatusType deserialize(final JsonParser json, final DeserializationContext context) throws IOException {
            final int statusCode = json.getIntValue();
            return Optional.ofNullable(index.get(statusCode))
                    .orElse(new UnknownStatus(statusCode));
        }

    }

}