package com.techsol.web.handlers;

import java.io.IOException;

import com.techsol.web.HTTPRequest;
import com.techsol.web.HTTPResponse;

public interface HTTPHandler {
    void handle(HTTPRequest httpRequest, HTTPResponse httpResponse) throws IOException;
}
