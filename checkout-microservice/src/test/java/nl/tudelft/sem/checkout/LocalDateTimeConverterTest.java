package nl.tudelft.sem.checkout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import nl.tudelft.sem.checkout.domain.LocalDateTimeConverter;
import org.apache.tomcat.jni.Local;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalDateTimeConverterTest {

    private LocalDateTimeConverter ldtConverter;

    @BeforeEach
    public void setup() {
        ldtConverter = new LocalDateTimeConverter();
    }

    @Test
    public void convertToColumnTest() {
        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2022, 5, 12), LocalTime.NOON);

        Assertions.assertThat(ldtConverter.convertToDatabaseColumn(ldt)).isEqualTo("2022-05-12 12:00");
    }

    @Test
    public void convertToLdtTest() {
        String ldtString = "2022-05-12 12:00";
        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2022, 5, 12), LocalTime.NOON);

        Assertions.assertThat(ldtConverter.convertToEntityAttribute(ldtString)).isEqualTo(ldt);
    }

}
