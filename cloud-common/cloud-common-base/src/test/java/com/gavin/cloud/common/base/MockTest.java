package com.gavin.cloud.common.base;

import com.gavin.cloud.common.base.util.NanoIdUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class MockTest {

    @Test
    public void testMockStatic() {
        // Mock scope
        try (MockedStatic<NanoIdUtils> mocked = mockStatic(NanoIdUtils.class)) {
            mocked.when(NanoIdUtils::randomNanoId).thenReturn("0123456789");
            assertEquals("0123456789", NanoIdUtils.randomNanoId());
        }
    }

}
