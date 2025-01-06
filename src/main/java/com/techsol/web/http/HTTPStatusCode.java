package com.techsol.web.http;

public enum HTTPStatusCode {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    FORBIDDEN(403, "Forbidden"),
    UNAUTHORIZED(401, "Unauthorized"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_TEMPORARILY_UNAVAILABLE(503, "Service Temporarily Unavailable"),
    GATEWAY_TEMPORARILY_UNAVAILABLE(504, "Gateway Temporarily Unavailable"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    EARLY_HINTS(103, "Early Hints");

    private final Integer statusCode;
    private final String reasonPhrase;

    HTTPStatusCode(Integer statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public static HTTPStatusCode fromStatusCode(int statusCode) {
        for (HTTPStatusCode httpStatusCode : HTTPStatusCode.values()) {
            if (httpStatusCode.getStatusCode() == statusCode) {
                return httpStatusCode;
            }
        }
        return null;
    }
}
