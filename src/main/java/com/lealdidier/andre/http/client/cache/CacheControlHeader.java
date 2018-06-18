package com.lealdidier.andre.http.client.cache;

public interface CacheControlHeader {
    int maxAge();

    boolean noStore();
}
