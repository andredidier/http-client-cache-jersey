package com.lealdidier.andre.http.client.cache.jersey.sun;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.dsl.RequestMatcherBuilder;
import io.specto.hoverfly.junit.dsl.StubServiceBuilder;
import io.specto.hoverfly.junit5.HoverflyExtension;
import io.specto.hoverfly.junit5.api.HoverflyConfig;
import io.specto.hoverfly.junit5.api.HoverflyCore;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.badRequest;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static io.specto.hoverfly.junit.verification.HoverflyVerifications.times;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*@HoverflySimulate(source = @HoverflySimulate.Source(value = "test", type = HoverflySimulate.SourceType.CLASSPATH),
        config = @HoverflyConfig(adminPort = 8088, proxyPort = 8080))*/
@HoverflyCore(config = @HoverflyConfig(adminPort = 10111, proxyPort = 10101))
@ExtendWith(HoverflyExtension.class)
public class JerseyCacheTest {

    /*
    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(dsl(
            service("test")
                    .get("/v1/cached-call")
                    .willReturn(success("{\"message\": \"ok\" }", "application/json"))
    ));
    */

    @Test
    @DisplayName("Test cache miss, one call")
    public void testCacheMiss(Hoverfly hoverfly) {
        String r = "{ \"message\": \"ok\" }";
        RequestMatcherBuilder b = service("test").get("/v1/cache-miss");
        hoverfly.simulate(SimulationSource.dsl(b
                .willReturn(success(r, "application/json"))));
        Client c = Client.create();
        c.addFilter(new CacheFilter());
        ClientResponse cr = c.resource("http://test/v1/cache-miss").get(ClientResponse.class);
        assertEquals(200, cr.getStatus());
        assertEquals(r, cr.getEntity(String.class));
        hoverfly.verify(b, times(1));
    }

    @Test
    @DisplayName("Test cache hit, public Cache-Control, no params")
    public void testCacheHit(Hoverfly hoverfly) {
        String r = "{ \"message\": \"ok\" }";
        RequestMatcherBuilder b = service("test").get("/v1/cached-noparams");
        hoverfly.simulate(SimulationSource.dsl(
                b.willReturn(success(r, "application/json")
                        .header("Cache-Control", "max-age=10"))
        ));
        Client c = Client.create();
        c.addFilter(new CacheFilter());
        WebResource res = c.resource("http://test/v1/cached-noparams");

        ClientResponse cr1 = res.get(ClientResponse.class);
        assertEquals(200, cr1.getStatus());
        assertEquals(r, cr1.getEntity(String.class));

        ClientResponse cr2 = res.get(ClientResponse.class);
        assertEquals(200, cr2.getStatus());
        assertEquals(r, cr2.getEntity(String.class));

        hoverfly.verify(b, times(1));
    }

    @Test
    @DisplayName("Test cache hit, private Cache-Control, no params")
    public void testCacheHitPrivateCache(Hoverfly hoverfly) {
        String r = "{ \"message\": \"ok\" }";
        RequestMatcherBuilder b = service("test").get("/v1/private-cached-noparams");
        hoverfly.simulate(SimulationSource.dsl(
                b.willReturn(success(r, "application/json")
                        .header("Cache-Control", "private,max-age=10"))
        ));
        Client c = Client.create();
        c.addFilter(new CacheFilter());
        WebResource res = c.resource("http://test/v1/private-cached-noparams");

        ClientResponse cr1 = res.get(ClientResponse.class);
        assertEquals(200, cr1.getStatus());
        assertEquals(r, cr1.getEntity(String.class));

        ClientResponse cr2 = res.get(ClientResponse.class);
        assertEquals(200, cr2.getStatus());
        assertEquals(r, cr2.getEntity(String.class));

        hoverfly.verify(b, times(1));
    }
    @Test
    @DisplayName("Test cache hit, public Cache-Control, with params")
    public void testCacheHitWithParams(Hoverfly hoverfly) {
        String r = "{ \"message\": \"ok\" }";
        RequestMatcherBuilder b = service("test").get("/v1/cached-call").queryParam("param1", "value1");
        hoverfly.simulate(SimulationSource.dsl(
                b.willReturn(success(r, "application/json")
                        .header("Cache-Control", "max-age=10"))
        ));
        Client c = Client.create();
        c.addFilter(new CacheFilter());
        WebResource res = c.resource("http://test/v1/cached-call").queryParam("param1", "value1");

        ClientResponse cr1 = res.get(ClientResponse.class);
        assertEquals(200, cr1.getStatus());
        assertEquals(r, cr1.getEntity(String.class));

        ClientResponse cr2 = res.get(ClientResponse.class);
        assertEquals(200, cr2.getStatus());
        assertEquals(r, cr2.getEntity(String.class));


        hoverfly.verify(b, times(1));
    }


    @Test
    @DisplayName("Test cache miss, expired cache")
    @Tag(TestTag.Slow)
    public void testCacheMissExpiredCache(Hoverfly hoverfly) throws InterruptedException {
        String r = "{ \"message\": \"ok\" }";
        RequestMatcherBuilder b = service("test").get("/v1/expired-cache");
        hoverfly.simulate(SimulationSource.dsl(
                b.willReturn(success(r, "application/json")
                                .header("Cache-Control", "max-age=3"))
        ));

        Client c = Client.create();
        c.addFilter(new CacheFilter());
        WebResource res = c.resource("http://test/v1/expired-cache");

        ClientResponse cr1 = res.get(ClientResponse.class);
        assertEquals(200, cr1.getStatus());
        assertEquals(r, cr1.getEntity(String.class));

        Thread.sleep(4000);

        ClientResponse cr2 = res.get(ClientResponse.class);
        assertEquals(200, cr2.getStatus());
        assertEquals(r, cr2.getEntity(String.class));

        hoverfly.verify(b, times(2));
    }

    @Test
    @DisplayName("Test cache miss, no store")
    public void testCacheMissNoStore(Hoverfly hoverfly) throws InterruptedException {
        String r = "{ \"message\": \"ok\" }";
        RequestMatcherBuilder b = service("test").get("/v1/no-store-cache");
        hoverfly.simulate(SimulationSource.dsl(
                b.willReturn(success(r, "application/json")
                        .header("Cache-Control", "no-store"))
        ));

        Client c = Client.create();
        c.addFilter(new CacheFilter());
        WebResource res = c.resource("http://test/v1/no-store-cache");

        ClientResponse cr1 = res.get(ClientResponse.class);
        assertEquals(200, cr1.getStatus());
        assertEquals(r, cr1.getEntity(String.class));

        ClientResponse cr2 = res.get(ClientResponse.class);
        assertEquals(200, cr2.getStatus());
        assertEquals(r, cr2.getEntity(String.class));

        hoverfly.verify(b, times(2));
    }

    @Test
    @DisplayName("Test cache hit, post")
    public void testCacheHitPost(Hoverfly hoverfly) {
        String r = "{ \"message\": \"ok\" }";
        RequestMatcherBuilder b = service("test").post("/v1/post-cache");
        hoverfly.simulate(SimulationSource.dsl(
                b.willReturn(success(r, "application/json")
                        .header("Cache-Control", "private,max-age=10"))
        ));
        Client c = Client.create();
        c.addFilter(new CacheFilter());
        WebResource res = c.resource("http://test/v1/post-cache");

        ClientResponse cr1 = res.post(ClientResponse.class);
        assertEquals(200, cr1.getStatus());
        assertEquals(r, cr1.getEntity(String.class));

        ClientResponse cr2 = res.post(ClientResponse.class);
        assertEquals(200, cr2.getStatus());
        assertEquals(r, cr2.getEntity(String.class));

        hoverfly.verify(b, times(2));
    }
}
