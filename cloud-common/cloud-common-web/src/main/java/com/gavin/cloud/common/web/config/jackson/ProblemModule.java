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
import com.gavin.cloud.common.base.problem.Exceptional;
import com.gavin.cloud.common.base.problem.Problem;
import com.gavin.cloud.common.base.problem.StatusType;
import com.gavin.cloud.common.base.problem.ThrowableProblem;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public final class ProblemModule extends Module {

    private final boolean stackTraces;
    private final Map<Integer, StatusType> statusMap;

    public <E extends Enum<E> & StatusType> ProblemModule(boolean stackTraces, List<Class<? extends E>> statusTypes) {
        this.stackTraces = stackTraces;
        this.statusMap = buildStatusMap(statusTypes);
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
        module.setMixInAnnotation(Exceptional.class, stackTraces ? ExceptionalWithStacktraceMixin.class : ExceptionalMixin.class);
        module.setMixInAnnotation(Problem.class, ProblemMixIn.class);
        module.addSerializer(StatusType.class, new StatusTypeSerializer());
        module.addDeserializer(StatusType.class, new StatusTypeDeserializer(statusMap));
        module.setupModule(context);
    }

    private static <E extends Enum<E> & StatusType> Map<Integer, StatusType> buildStatusMap(List<Class<? extends E>> statusTypes) {
        final Map<Integer, StatusType> statusMap = new HashMap<>();
        for (Class<? extends E> statusType : statusTypes) {
            for (E status : statusType.getEnumConstants()) {
                if (Objects.nonNull(statusMap.putIfAbsent(status.getStatusCode(), status))) {
                    throw new IllegalArgumentException("Duplicate status codes are not allowed");
                }
            }
        }
        return statusMap;
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

    private static final class StatusTypeSerializer extends JsonSerializer<StatusType> {

        @Override
        public void serialize(StatusType status, JsonGenerator json, SerializerProvider serializers) throws IOException {
            json.writeNumber(status.getStatusCode());
        }

    }

    private static final class StatusTypeDeserializer extends JsonDeserializer<StatusType> {

        private final Map<Integer, StatusType> statusMap;

        StatusTypeDeserializer(Map<Integer, StatusType> statusMap) {
            this.statusMap = statusMap;
        }

        @Override
        public StatusType deserialize(JsonParser json, DeserializationContext context) throws IOException {
            final int statusCode = json.getIntValue();
            return statusMap.getOrDefault(statusCode, new UnknownStatus(statusCode));
        }

    }

    private static final class UnknownStatus implements StatusType {

        private final int statusCode;

        public UnknownStatus(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String getReasonPhrase() {
            return "Unknown";
        }

    }

}