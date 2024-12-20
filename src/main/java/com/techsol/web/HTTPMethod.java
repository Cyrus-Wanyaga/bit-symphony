package com.techsol.web;

public enum HTTPMethod {
    // Only retrieves data
    GET("GET"),
    // Same as GET but transfers only the status line and the header section
    HEAD("HEAD"),
    // Used to send data to the server
    POST("POST"),
    // Replaces the current representations of the target resource with the uploaded
    // content
    PUT("PUT"),
    // Removes all the current representations of the target resource given by a URI
    DELETE("DELETE"),
    // Establishes a tunnel to the server identified by a given URI
    CONNECT("CONNECT"),
    // Describe the communication options for the target resource
    OPTIONS("OPTIONS"),
    // Performs a message loop back test along with the path to the target resource
    TRACE("TRACE");

    private final String value;

    HTTPMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HTTPMethod fromValue(String value) {
        for (HTTPMethod httpMethod : HTTPMethod.values()) {
            if (httpMethod.getValue().equals(value)) {
                return httpMethod;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}