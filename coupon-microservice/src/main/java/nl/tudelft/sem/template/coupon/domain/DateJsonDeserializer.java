package nl.tudelft.sem.template.coupon.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.ParseException;

/**
 * Custom json deserializer that converts a string to a Date.
 */
public class DateJsonDeserializer extends StdDeserializer<Date> {

    private static final int serialVersionUID = -1274771824;

    protected DateJsonDeserializer() {
        super(Date.class);
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String[] date = p.getText().split("/");
        try {
            return new Date(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
