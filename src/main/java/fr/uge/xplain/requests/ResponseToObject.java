package fr.uge.xplain.requests;

import java.util.List;
import java.util.Objects;

/**
 * Represents a response object that contains a list of error messages.
 * This record is used to encapsulate compilation errors to be sent back to the client.
 *
 * @param response a list of error messages.
 *               It cannot be null, and it is defensively copied to ensure immutability.
 */
public record ResponseToObject <E> (List<E> response) {

    /**
     * Constructs a {@code ResponseToObject} instance with the specified list of errors.
     *
     * @param response the list of error messages, must not be null.
     * @throws NullPointerException if the provided {@code errors} list is null.
     */
    public ResponseToObject {
        Objects.requireNonNull(response);
        response = List.copyOf(response);
    }

}