package com.lealdidier.andre.http.client.cache.jersey.sun;

import com.lealdidier.andre.http.client.cache.CacheControlHeader;
import com.sun.jersey.api.client.ClientResponse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientResponseCacheControlHeader implements CacheControlHeader {

    private ClientResponse response;
    private transient int maxAge;
    private boolean headerRead;
    private boolean noStore;

    public ClientResponseCacheControlHeader(ClientResponse response) {
        this.response = response;
    }

    @Override
    public int maxAge() {
        if (!headerRead) {
            readHeader();
        }
        return maxAge;
    }

    @Override
    public boolean noStore() {
        if (!headerRead) {
            readHeader();
        }
        return noStore;
    }

    private synchronized void readHeader() {
        List<String> values = response.getHeaders().get("Cache-Control");
        if (values != null && !values.isEmpty()) {
            processValues(values);
        }
        this.headerRead = true;
    }

    private void processValues(List<String> values) {
        String headerValue = values.get(0);
        Matcher noStoreMatcher = Pattern.compile("no-store").matcher(headerValue);
        this.noStore = noStoreMatcher.matches();

        Matcher maxAgeMatcher = Pattern.compile("(?:private,|)\\s*max-age\\s*=\\s*(\\d+)").matcher(headerValue);
        if (maxAgeMatcher.matches()) {
            this.maxAge = Integer.parseInt(maxAgeMatcher.group(1));
        }
    }
}
