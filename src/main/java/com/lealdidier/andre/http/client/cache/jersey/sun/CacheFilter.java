package com.lealdidier.andre.http.client.cache.jersey.sun;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CacheFilter extends ClientFilter {

    private final static Map<CacheKey, ClientRequestCache> cache = new TreeMap<>();

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (cr.getMethod().equals("GET")) {
            return handleGetRequest(cr);
        }
        return getNext().handle(cr);
    }

    private ClientResponse handleGetRequest(ClientRequest cr) {
        ClientRequestCacheKey key = new ClientRequestCacheKey(cr);
        ClientRequestCache crCache = cache.get(key);
        if (crCache == null || crCache.isExpired()) {
            crCache = new ClientRequestCache(getNext().handle(cr));
            cache.put(key, crCache);
        }
        return crCache.cached();
    }
}
