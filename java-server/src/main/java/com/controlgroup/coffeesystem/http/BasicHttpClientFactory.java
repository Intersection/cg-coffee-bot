package com.controlgroup.coffeesystem.http;

import com.controlgroup.coffeesystem.interfaces.HttpClientFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Created by timmattison on 12/31/14.
 */
public class BasicHttpClientFactory implements HttpClientFactory {
    @Override
    public HttpClient create() {
        return HttpClientBuilder.create().build();
    }
}
