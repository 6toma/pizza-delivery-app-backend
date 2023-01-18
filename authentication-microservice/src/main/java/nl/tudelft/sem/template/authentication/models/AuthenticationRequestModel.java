package nl.tudelft.sem.template.authentication.models;

import lombok.Data;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.Password;

/**
 * Model representing an authentication request.
 */
@Data
public class AuthenticationRequestModel {
    private NetId netId;
    private Password password;
}