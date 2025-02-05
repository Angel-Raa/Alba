package com.github.alba.handler;

import com.github.alba.core.Request;
import com.github.alba.core.Response;

@FunctionalInterface
public interface Handler {
    Response handle(Request request);
}
