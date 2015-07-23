package com.controlgroup.coffeesystem.interfaces;

import org.apache.http.client.HttpClient;

/**
 * Created by timmattison on 12/31/14.
 */
public interface HttpClientFactory {
    public HttpClient create();
}
