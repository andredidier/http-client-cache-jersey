package com.lealdidier.andre.http.client.cache.jersey.sun;

import com.lealdidier.andre.http.client.cache.CacheControlValueExpirationDate;
import com.lealdidier.andre.http.client.cache.ExpirationDate;
import com.sun.jersey.api.client.ClientResponse;

public class ExpirationDateOf implements ExpirationDate {

    private ExpirationDate inner;

    public ExpirationDateOf(ExpirationDate inner) {
        this.inner = inner;
    }

    public ExpirationDateOf(ClientResponse clientResponse) {
        this(new CacheControlValueExpirationDate(new ClientResponseCacheControlHeader(clientResponse)));
    }

    @Override
    public boolean isExpired() {
        return inner.isExpired();
    }
}
