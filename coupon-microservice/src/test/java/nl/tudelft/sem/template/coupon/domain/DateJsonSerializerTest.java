package nl.tudelft.sem.template.coupon.domain;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DateJsonSerializerTest {

    private JsonGenerator jsonGenerator = Mockito.mock(JsonGenerator.class);
    private DateJsonSerializer dateJsonSerializer = new DateJsonSerializer();

    @SneakyThrows
    @Test
    void serialize() {
        dateJsonSerializer.serialize(new Date(10, 10, 2025), jsonGenerator, null);
        verify(jsonGenerator).writeString("10/10/2025");
    }
}