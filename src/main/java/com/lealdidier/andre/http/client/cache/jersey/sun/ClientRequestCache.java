package com.lealdidier.andre.http.client.cache.jersey.sun;

import com.lealdidier.andre.http.client.cache.ExpirationDate;
import com.sun.jersey.api.client.ClientResponse;

public class ClientRequestCache {
    private ExpirationDate expiresAt;
    private CachedClientResponse cachedClientResponse;

    public ClientRequestCache(ClientResponse clientResponse) {
        this(new ExpirationDateOf(clientResponse), new CachedClientResponse(clientResponse));
    }


    public ClientRequestCache(ExpirationDate expiresAt, CachedClientResponse cachedClientResponse) {
        this.expiresAt = expiresAt;
        this.cachedClientResponse = cachedClientResponse;
    }

    public ClientResponse cached() {
        return cachedClientResponse;
    }

    public boolean isExpired() {
        return expiresAt.isExpired();
    }
}
