package com.lealdidier.andre.http.client.cache.jersey.sun;

import com.sun.jersey.api.client.*;

import javax.ws.rs.core.*;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class CachedClientResponse extends ClientResponse {
    private ClientResponse inner;
    private Map<Class<?>, Object> classMap = new HashMap<>();
    private Map<GenericType<?>, Object> genericTypeMap = new HashMap<>();
    public CachedClientResponse(ClientResponse inner) {
        super(inner.getStatus(), null, null, null);
        this.inner = inner;
    }

    @Override
    public Client getClient() {
        return inner.getClient();
    }

    @Override
    public Map<String, Object> getProperties() {
        return inner.getProperties();
    }

    @Override
    public int getStatus() {
        return inner.getStatus();
    }

    @Override
    public void setStatus(int status) {
        inner.setStatus(status);
    }

    @Override
    public void setStatus(Response.StatusType statusType) {
        inner.setStatus(statusType);
    }

    @Override
    public MultivaluedMap<String, String> getHeaders() {
        return inner.getHeaders();
    }

    @Override
    public boolean hasEntity() {
        return inner.hasEntity();
    }

    @Override
    public InputStream getEntityInputStream() {
        return inner.getEntityInputStream();
    }

    @Override
    public void setEntityInputStream(InputStream entity) {
        inner.setEntityInputStream(entity);
    }

    @Override
    public <T> T getEntity(Class<T> c) throws ClientHandlerException, UniformInterfaceException {
        if (!classMap.containsKey(c)) {
            classMap.put(c, inner.getEntity(c));
        }
        return (T) classMap.get(c);
    }

    @Override
    public <T> T getEntity(GenericType<T> gt) throws ClientHandlerException, UniformInterfaceException {
        if (!genericTypeMap.containsKey(gt)) {
            genericTypeMap.put(gt, inner.getEntity(gt));
        }
        return (T) genericTypeMap.get(gt);
    }

    @Override
    public void bufferEntity() throws ClientHandlerException {
        inner.bufferEntity();
    }

    @Override
    public void close() throws ClientHandlerException {
        inner.close();
    }

    @Override
    public MediaType getType() {
        return inner.getType();
    }

    @Override
    public URI getLocation() {
        return inner.getLocation();
    }

    @Override
    public EntityTag getEntityTag() {
        return inner.getEntityTag();
    }

    @Override
    public Date getLastModified() {
        return inner.getLastModified();
    }

    @Override
    public Date getResponseDate() {
        return inner.getResponseDate();
    }

    @Override
    public String getLanguage() {
        return inner.getLanguage();
    }

    @Override
    public int getLength() {
        return inner.getLength();
    }

    @Override
    public List<NewCookie> getCookies() {
        return inner.getCookies();
    }

    @Override
    public Set<String> getAllow() {
        return inner.getAllow();
    }

    @Override
    public WebResourceLinkHeaders getLinks() {
        return inner.getLinks();
    }

    @Override
    public String toString() {
        return inner.toString();
    }

    @Override
    public int hashCode() {
        return inner.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return inner.equals(obj);
    }
}
