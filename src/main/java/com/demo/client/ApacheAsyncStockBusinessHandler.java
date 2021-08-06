package com.demo.client;

import com.squareup.okhttp.MediaType;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Method;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ApacheAsyncStockBusinessHandler implements StockBusinessHandler {

   private String priceUrl;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final Random rand = new Random();
    private final CloseableHttpAsyncClient client;
    private final PoolingAsyncClientConnectionManager connectionManager;
    private final RequestConfig requestConfig;

    public ApacheAsyncStockBusinessHandler(HttpClientConfiguration config) {
        this.priceUrl = config.getServerGetUrl();
        connectionManager = new PoolingAsyncClientConnectionManager();
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getConnectTimeOut(), TimeUnit.MILLISECONDS)
                .setDefaultKeepAlive(config.getKeepAliveTime(), TimeUnit.MILLISECONDS)
                .setResponseTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
        connectionManager.setMaxTotal(config.getMaxConnection());
        client = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
        client.start();
    }

    @Override
    public CompletableFuture<String> getPrice() {
        log.info("Call from Apache async");
        SimpleHttpRequest request1 = SimpleRequestBuilder.create(Method.GET)
                .setHeader("Accept", "application/json")
                .setHeader("Content-type", "application/json")
                .setUri(priceUrl).build();
        CompletableFuture<String> result = new CompletableFuture<>();
        client.execute(request1, new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse response) {
                try {
                    result.complete(response.getBodyText());
                } catch (Exception ex) {
                    result.completeExceptionally(ex);
                }
            }

            @Override
            public void failed(Exception ex) {
                result.completeExceptionally(ex);
            }

            @Override
            public void cancelled() {
                result.completeExceptionally(new Exception("request canceled!!!"));
            }
        });
        return result;
    }


}
