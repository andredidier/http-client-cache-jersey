package com.lealdidier.andre.http.client.cache.jersey.sun;

import com.sun.jersey.api.client.ClientRequest;

import java.net.URI;

public class ClientRequestCacheKey implements CacheKey {

    private ClientRequest request;

    public ClientRequestCacheKey(ClientRequest clientRequest) {
        this.request = clientRequest;
    }

    @Override
    public int compareTo(CacheKey o) {
        if (!(o instanceof ClientRequestCacheKey)) {
            return -1;
        }
        URI thisUri = request.getURI();
        URI thatUri = ((ClientRequestCacheKey)o).request.getURI();

        return thisUri.compareTo(thatUri);
    }
}
