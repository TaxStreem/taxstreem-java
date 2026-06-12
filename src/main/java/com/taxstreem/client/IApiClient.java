package com.taxstreem.client;

public interface IApiClient {
    String request(String method, String path, Object body) throws Exception;
}
