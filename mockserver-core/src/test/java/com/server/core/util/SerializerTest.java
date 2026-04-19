package com.server.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SerializerTest {
    @Test
    void serializeAndDeserializePojoRoundTrip() {
        SamplePojo input = new SamplePojo("mock-server", 2);

        String json = Serializer.serialize(input).orElseThrow();
        Optional<SamplePojo> actual = Serializer.deSerialize(json, SamplePojo.class);

        assertTrue(actual.isPresent());
        assertEquals("mock-server", actual.get().name);
        assertEquals(2, actual.get().count);
    }

    @Test
    void deSerializeReturnsEmptyForBlankInput() {
        assertTrue(Serializer.deSerialize("  ", SamplePojo.class).isEmpty());
    }

    @Test
    void serializeListAndDeserializeListRoundTrip() {
        List<SamplePojo> input = List.of(new SamplePojo("one", 1), new SamplePojo("two", 2));

        String json = Serializer.serializeList(input).orElseThrow();
        Optional<List<SamplePojo>> actual = Serializer.deSerializeList(json, SamplePojo.class);

        assertTrue(actual.isPresent());
        assertEquals(2, actual.get().size());
        assertEquals("one", actual.get().get(0).name);
    }

    @Test
    void serializeListReturnsEmptyForEmptyInput() {
        assertTrue(Serializer.serializeList(List.of()).isEmpty());
    }

    @Test
    void deSerializeListReturnsEmptyForBlankInput() {
        assertTrue(Serializer.deSerializeList(" ", SamplePojo.class).isEmpty());
    }

    static class SamplePojo {
        public String name;
        public int count;

        public SamplePojo() {
        }

        SamplePojo(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }
}
