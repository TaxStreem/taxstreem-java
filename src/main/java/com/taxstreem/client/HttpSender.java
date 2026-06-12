package com.taxstreem.client;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@FunctionalInterface
interface HttpSender {
    HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException;
}
