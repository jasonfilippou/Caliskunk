package com.company.rest.products.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simple POJO to provide the user with the data they requested, a human-readable message and an {@link org.springframework.http.HttpStatus}
 * indicating the HTTP Code for their query.
 *
 * @author Chris Taitt, edits by Jason Filippou on 11-10-2020
 */
// Annotations added by Jason to avoid boilerplate setters / getters.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {

    /**
     * A human - readable message indicating a successful API call.
     */
    final public static String SUCCESS = "SUCCESS";

    /**
     * A human - readable message indicating an <b>un</b>successful API call.
     */
    final public static String FAILURE = "FAILURE";

    private String status;
    private String message;
    private Object data;
}
