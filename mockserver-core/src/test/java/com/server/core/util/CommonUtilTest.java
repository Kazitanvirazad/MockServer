package com.server.core.util;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static com.server.core.constants.CommonConstants.DEFAULT_ID_LENGTH;
import static com.server.core.constants.CommonConstants.TRACER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonUtilTest {
    @Test
    void generateUniqueAlphanumericIdUsesDefaultLength() {
        String actual = CommonUtil.generateUniqueAlphanumericId().orElseThrow();

        assertEquals(DEFAULT_ID_LENGTH, actual.length());
        assertTrue(actual.matches("[A-Za-z0-9]+"));
    }

    @Test
    void generateUuid7BasedIdRemovesHyphenAndUpperCases() {
        String actual = CommonUtil.generateUUID7BasedId().orElseThrow();

        assertFalse(actual.contains("-"));
        assertEquals(actual.toUpperCase(), actual);
    }

    @Test
    void getRandomNumberInRangeReturnsValueWithinBounds() throws Exception {
        int actual = CommonUtil.getRandomNumberInRange(10, 20);

        assertTrue(actual >= 10);
        assertTrue(actual < 20);
    }

    @Test
    void setAndRemoveLogTracerManageMdcValue() {
        CommonUtil.setLogTracer();
        assertFalse(MDC.get(TRACER).isBlank());

        CommonUtil.removeLogTracer();
        assertEquals(null, MDC.get(TRACER));
    }

    @Test
    void removeLogTracerDoesNothingWhenMissing() {
        MDC.remove(TRACER);

        CommonUtil.removeLogTracer();

        assertEquals(null, MDC.get(TRACER));
    }
}
