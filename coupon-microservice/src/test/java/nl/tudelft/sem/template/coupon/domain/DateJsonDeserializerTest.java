package nl.tudelft.sem.template.coupon.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DateJsonDeserializerTest {

    private DateJsonDeserializer dateJsonDeserializer = new DateJsonDeserializer();
    private JsonParser jsonParser = Mockito.mock(JsonParser.class);

    @SneakyThrows
    @Test
    void deserialize() {
        when(jsonParser.getText()).thenReturn("10/10/2025");
        assertThat(dateJsonDeserializer.deserialize(jsonParser, null)).isEqualTo(new Date(10, 10, 2025));
    }

    @SneakyThrows
    @Test
    void deserializeParseException() {
        when(jsonParser.getText()).thenReturn("a/10/2025");
        assertThrows(RuntimeException.class, () -> dateJsonDeserializer.deserialize(jsonParser,null));
    }

    @SneakyThrows
    @Test
    void deserializeInvalidDate() {
        when(jsonParser.getText()).thenReturn("31/02/2025");
        assertThrows(RuntimeException.class, () -> dateJsonDeserializer.deserialize(jsonParser,null));
    }
}