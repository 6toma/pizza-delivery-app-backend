package nl.tudelft.sem.template.commons.utils;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class RequestHelper {
    private static final Logger logger = LoggerFactory.getLogger(RequestHelper.class);
    // TODO: Generate a new token (make it fast and easy for now)
    private final AuthManager authenticationManager;

    private final String baseUrl = "http://localhost";

    /**
     * Does a <b>POST</b> request to another microservice.
     *
     * @param port          the port that the other microservice is running on. Check application.properties of the other
     *                      microservice to see the port number
     * @param path          the path of the other microservice /store for instance
     * @param toSend        the object that you want to send to the other microservice
     * @param responseClass the class type of the response we expect. If the other microservice sends String then it
     *                      <code>String.class</code>. If the other microservice send <code>List</code> then it is
     *                      <code>List.class</code>
     * @param <T>           The type of the object that we are going to receive
     * @return a spring Response entity
     */
    public <T> ResponseEntity<T> postRequest(int port, String path, Object toSend, Class<T> responseClass) {
        return doRequest(RequestType.POST, port, path, toSend, responseClass);
    }

    /**
     * Does a <b>GET</b> request to another microservice.
     *
     * @param port          the port that the other microservice is running on. Check application.properties of the other
     *                      microservice to see the port number
     * @param path          the path of the other microservice /store for instance
     * @param responseClass the class type of the response we expect. If the other microservice sends String then it
     *                      <code>String.class</code>. If the other microservice send <code>List</code> then it is
     *                      <code>List.class</code>
     * @param <T>           The type of the object that we are going to receive
     * @return a spring Response entity
     */
    public <T> ResponseEntity<T> getRequest(int port, String path, Class<T> responseClass) {
        return doRequest(RequestType.GET, port, path, null, responseClass);
    }

    /**
     * Does a <b>DELETE</b> request to another microservice.
     *
     * @param port          the port that the other microservice is running on. Check application.properties of the other
     *                      microservice to see the port number
     * @param path          the path of the other microservice /store for instance
     * @param responseClass the class type of the response we expect. If the other microservice sends String then it
     *                      <code>String.class</code>. If the other microservice send <code>List</code> then it is
     *                      <code>List.class</code>
     * @param <T>           The type of the object that we are going to receive
     * @return a spring Response entity
     */
    public <T> ResponseEntity<T> deleteRequest(int port, String path, Class<T> responseClass) {
        return doRequest(RequestType.DELETE, port, path, null, responseClass);
    }

    // TODO a better solution to handle post request. Right now toSend is simply null if the request is GET or DELETE
    private <T> ResponseEntity<T> doRequest(RequestType requestType, int port, String path, Object toSend,
                                            Class<T> responseClass) {
        URI url = createUrl(port, path);
        logger.info("Doing a POST on " + url);
        RequestEntity.HeadersBuilder<?> request;
        try {
            switch (requestType) {
                case GET:
                    request = RequestEntity.get(url).accept(MediaType.APPLICATION_JSON);
                    break;
                case POST:
                    request = RequestEntity.post(url).contentType(MediaType.APPLICATION_JSON);
                    break;
                case DELETE:
                    request = RequestEntity.delete(url).accept(MediaType.APPLICATION_JSON);
                    break;
                default:
                    throw new IllegalArgumentException("Method not supported");
            }
            var requestWithToken = request.header("Authorization", "Bearer " + authenticationManager.getJwtToken());
            if (toSend != null) {
                // add to post request the object
                var postRequest = ((RequestEntity.BodyBuilder) requestWithToken).body(toSend);
                return new RestTemplate().exchange(postRequest, responseClass);
            }
            return new RestTemplate().exchange((RequestEntity<?>) requestWithToken, responseClass);
        } catch (ResourceAccessException connectException) {
            logger.error("The other microservice can't be reached. Check if port is ok or the path is ok.");
            throw new IllegalArgumentException("The url " + url +
                " is not valid,copy url to postman to check. Maybe the other server is not running or the path is not good");
        }
    }

    private URI createUrl(int port, String path) {
        String url = String.format("%s:%d%s", baseUrl, port, path);
        return URI.create(url);
    }

    enum RequestType {
        POST, GET, DELETE
    }
}
