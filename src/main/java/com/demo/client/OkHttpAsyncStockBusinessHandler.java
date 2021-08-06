package com.demo.client;

import com.demo.client.util.CompletableFutureUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.okhttp.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.demo.client.util.CompletableFutureUtils.failedFuture;
import static com.demo.client.util.JsonUtils.MAPPER;

@Log4j2
public class OkHttpAsyncStockBusinessHandler implements StockBusinessHandler {

   private String priceUrl;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final Random rand = new Random();
    private final OkHttpClient client;

    public OkHttpAsyncStockBusinessHandler(HttpClientConfiguration config) {
        this.priceUrl = config.getServerGetUrl();
        ConnectionPool connectionPool = new ConnectionPool(config.getMaxConnection(), config.getKeepAliveTime());
        this.client = new OkHttpClient();
        client.setConnectionPool(connectionPool);
        client.setConnectTimeout(config.getConnectTimeOut(), TimeUnit.MILLISECONDS);
        client.setReadTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS);
        client.setWriteTimeout(config.getWriteTimeout(), TimeUnit.MILLISECONDS);
    }

    @Override
    public CompletableFuture<String> getPrice() {
        log.info("Call from ok http async");
        Request request = new Request.Builder()
                .url(priceUrl)
                .get()
                .build();
        Call call = client.newCall(request);
        CompletableFuture<String> result = new CompletableFuture<>();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                result.completeExceptionally(e);

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        result.complete(response.body().string());
                        return;
                    }
                    result.completeExceptionally(new IllegalArgumentException("request is not in right format"));
                } catch (Exception ex) {
                    result.completeExceptionally(ex);
                }
            }

        });
        return result;
    }


}
