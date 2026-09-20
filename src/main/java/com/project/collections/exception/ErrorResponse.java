package com.project.collections.exception;

import java.util.List;

/**
 * Common error body returned by the API.
 */
public record ErrorResponse(

        int code,
        String message,
        List<String> details

) {

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message, List.of());
    }

}
