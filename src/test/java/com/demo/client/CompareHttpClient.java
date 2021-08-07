package com.demo.client;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompareHttpClient {


    public static final String URL = "http://localhost:8080";
    public static final int NUM_OF_REQUEST = 1;

    public static void startServer() {
        HttpServer server = Vertx.vertx().createHttpServer();

        server.requestHandler(request -> {

            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("Hello World!");
        });

        server.listen(8080);

    }

    @BeforeAll
    static void beforeAll() {
        startServer();
    }

    public static void callServer(HttpClientConfiguration configuration) throws ExecutionException, InterruptedException {
        StockBusinessHandler handler;
        switch (configuration.getClientType()) {
            case APACHE_SYNC:
                handler = new ApacheSyncStockBusinessHandler(configuration);
                break;
            case APACHE_ASYNC:
                handler = new ApacheAsyncStockBusinessHandler(configuration);
                break;
            case OKHTTP_SYNC:
                handler = new OkHttpSyncStockBusinessHandler(configuration);
                break;
            case OKHTTP_ASYNC:
                handler = new OkHttpAsyncStockBusinessHandler(configuration);
                break;
            default:
                throw new IllegalArgumentException("Unknown client type");
        }
        CompletableFuture[] all = new CompletableFuture[NUM_OF_REQUEST];
        for (int i = 0; i < NUM_OF_REQUEST; i++) {
            all[i] = handler.getPrice();
        }
        CompletableFuture.allOf(all).get();
    }

    @Test
    public void apacheSync() throws Exception {

        HttpClientConfiguration config = HttpClientConfiguration.builder()
                .clientType(HttpClientConfiguration.ClientType.APACHE_SYNC)
                .maxConnection(10)
                .keepAliveTime(1000)
                .connectTimeOut(1000)
                .readTimeout(1000)
                .writeTimeout(1000)
                .serverGetUrl(URL)
                .build();

        callServer(config);

    }

    @Test
    public void apacheAsync() throws Exception {

        HttpClientConfiguration config = HttpClientConfiguration.builder()
                .clientType(HttpClientConfiguration.ClientType.APACHE_ASYNC)
                .maxConnection(10)
                .keepAliveTime(1000)
                .connectTimeOut(1000)
                .readTimeout(1000)
                .writeTimeout(1000)
                .serverGetUrl(URL)
                .build();

        callServer(config);

    }

    @Test
    public void okHttpSync() throws Exception {
        HttpClientConfiguration config = HttpClientConfiguration.builder()
                .clientType(HttpClientConfiguration.ClientType.OKHTTP_SYNC)
                .maxConnection(10)
                .keepAliveTime(1000)
                .connectTimeOut(1000)
                .readTimeout(1000)
                .writeTimeout(1000)
                .serverGetUrl(URL)
                .build();
        callServer(config);
    }

    @Test
    public void okHttpAsync() throws Exception {

        HttpClientConfiguration config = HttpClientConfiguration.builder()
                .clientType(HttpClientConfiguration.ClientType.OKHTTP_ASYNC)
                .maxConnection(10)
                .keepAliveTime(1000)
                .connectTimeOut(1000)
                .readTimeout(1000)
                .writeTimeout(1000)
                .serverGetUrl(URL)
                .build();

        callServer(config);

    }

}
