package nl.tudelft.sem.template.commons.utils;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.annotations.role.MicroServiceInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class RequestHelper {
    private static final Logger logger = LoggerFactory.getLogger(RequestHelper.class);
    // TODO: Generate a new token (make it fast and easy for now)
    private final JwtTokenGenerator jwtTokenGenerator;
    private String currentToken;
    private long tokenExpiryTime;

    // TODO : don't harcode
    private final String baseUrl = "http://localhost";

    // TODO a better solution to handle post request. Right now toSend is simply null if the request is GET or DELETE
    public <T> T doRequest(RequestObject requestObject, Object toSend, Class<T> responseClass) {
        URI url = createUrl(requestObject.getPort(), requestObject.getPath());
        logger.info("Doing a " + requestObject.getHttpMethod().toString() + " on " + url);
        try {
            RequestEntity.HeadersBuilder<?> request = getHeadersBuilder(requestObject, url);
            var requestWithToken = request.header("Authorization", "Bearer " + getAuthenticationToken());
            if (toSend != null) {
                // add to post request the object
                var postRequest = ((RequestEntity.BodyBuilder) requestWithToken).body(toSend);
                return new RestTemplate().exchange(postRequest, responseClass).getBody();
            }
            return new RestTemplate().exchange(requestWithToken.build(), responseClass).getBody();
        } catch (ResourceAccessException connectException) {
            logger.error("The other microservice can't be reached. Check if port is ok or the path is ok.");
            throw new IllegalArgumentException("The url " + url
                +
                " is not valid,copy url to postman to check. Maybe the other server is not running or the path is not good");
        }
    }

    public <T> T doRequest(RequestObject requestObject, Class<T> responseClass) {
        URI url = createUrl(requestObject.getPort(), requestObject.getPath());
        logger.info("Doing a " + requestObject.getHttpMethod().toString() + " on " + url);
        try {
            RequestEntity.HeadersBuilder<?> request = getHeadersBuilder(requestObject, url);
            var requestWithToken = request.header("Authorization", "Bearer " + getAuthenticationToken());
            return new RestTemplate().exchange(requestWithToken.build(), responseClass).getBody();
        } catch (ResourceAccessException connectException) {
            logger.error("The other microservice can't be reached. Check if port is ok or the path is ok.");
            throw new IllegalArgumentException("The url " + url + " is not valid,copy url to postman to check. Maybe the other server is not running or the path is not good");
        }
    }

    private <T> RequestEntity.HeadersBuilder<?> getHeadersBuilder(RequestObject requestObject, URI url) {
        return RequestEntity.method(requestObject.getHttpMethod(), url).accept(MediaType.APPLICATION_JSON);
    }

    private URI createUrl(int port, String path) {
        String url = String.format("%s:%d%s", baseUrl, port, path);
        return URI.create(url);
    }

    /**
     * Generates a token with the right authority to make requests to protected microservice interaction endpoints.
     *
     * @return The current token
     */
    private String getAuthenticationToken() {
        // If token is null (not generated yet) or it has expired, generate a new token
        if (currentToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
            currentToken = jwtTokenGenerator.generateToken(
                User.withUsername("microservice-interaction@microservice.com")
                    .authorities(MicroServiceInteraction.AUTHORITY)
                    .password("")
                    .build()
            );
            // Subtract one minute to give it some slack, we don't want microservice interactions to
            // fail due to the token being expired.
            tokenExpiryTime =
                System.currentTimeMillis() + JwtTokenGenerator.JWT_TOKEN_VALIDITY - TimeUnit.MINUTES.toMillis(1);
        }
        return currentToken;
    }
}
