package fr.uge.xplain.requests;

import java.util.Objects;

/**
 * Represents a request containing a piece of source code to be processed.
 * This record is used to encapsulate the input data sent to the backend for compilation analysis.
 *
 * @param code the source code provided by the user.
 *             It cannot be null, and a {@code NullPointerException} will be thrown if a null value is provided.
 */
public record CodeRequest(String code) {

    /**
     * Constructs a {@code CodeRequest} instance with the specified source code.
     *
     * @param code the source code to process, must not be null.
     * @throws NullPointerException if the provided {@code code} is null.
     */
    public CodeRequest {
        Objects.requireNonNull(code);
    }

}