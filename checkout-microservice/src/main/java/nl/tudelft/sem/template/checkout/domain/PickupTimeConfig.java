package nl.tudelft.sem.template.checkout.domain;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PickupTimeConfig {

    /**
     * Bean that sets up serialization/deserialization for the LocalDateTime in JSON.
     *
     * @return The customizer with our serialization/deserialization
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {

        return builder -> {

            // formatter
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // deserializers
            builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));

            // serializers
            builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
        };
    }
}
