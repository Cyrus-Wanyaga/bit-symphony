package com.techsol.web.handlers;

import java.io.IOException;

import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;

public interface HTTPHandler {
    void handle(HTTPRequest httpRequest, HTTPResponse httpResponse) throws IOException;
}
