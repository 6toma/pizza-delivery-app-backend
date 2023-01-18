package nl.tudelft.sem.template.authentication.models;

import lombok.Data;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.Password;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private NetId netId;
    private Password password;
}