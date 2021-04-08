package com.gavin.cloud.common.base;

import com.gavin.cloud.common.base.util.NanoIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@Slf4j
public class MockTest {

    @Test
    public void testMockStatic() {
        // Mock scope
        try (MockedStatic<NanoIdUtils> mocked = mockStatic(NanoIdUtils.class)) {
            mocked.when(NanoIdUtils::randomNanoId).thenReturn("0123456789");
            assertEquals("0123456789", NanoIdUtils.randomNanoId());
        }
    }

    /**
     * @see <a href="https://stackoverflow.com/questions/64463733/mockito-mock-a-static-void-method-with-mockito-mockstatic">Mockito Mock a static void method</a>
     */
    @Test
    public void testMockStaticReturnVoid() throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource("cert.jks");
        Path path = new File(Objects.requireNonNull(url).getFile()).toPath();
        // Mock scope
        try (MockedStatic<Files> mocked = mockStatic(Files.class)) {
            mocked.when(() -> Files.delete(any(Path.class))).thenAnswer(Answers.RETURNS_DEFAULTS);
            Files.delete(path);
            mocked.verify(() -> Files.delete(path));
        }
    }

}
