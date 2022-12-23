package nl.tudelft.sem.template.coupon.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * Custom json serializer that converts a date to a string formatted as: dd/MM/yyyy.
 */
public class DateJsonSerializer extends StdSerializer<Date> {

    private static final int serialVersionUID = -1274718231;

    protected DateJsonSerializer() {
        super(Date.class);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getDay() + "/" + value.getMonth() + "/" + value.getYear());
    }
}
