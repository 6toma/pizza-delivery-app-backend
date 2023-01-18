package nl.tudelft.sem.template.commons.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
@AllArgsConstructor
public class RequestObject {
    private HttpMethod httpMethod;
    private int port;
    private String path;
}
