package com.demo.client;

import com.demo.client.util.CompletableFutureUtils;
import com.squareup.okhttp.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
public class OkHttpSyncStockBusinessHandler implements StockBusinessHandler {

   private String priceUrl;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final Random rand = new Random();
    private final OkHttpClient client;

    public OkHttpSyncStockBusinessHandler(HttpClientConfiguration config) {
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
        log.info("Call from ok http sync");
        Request request = new Request.Builder()
                .url(priceUrl)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return CompletableFutureUtils.failedFuture(new Exception("failed request"));
            }
            return CompletableFuture.completedFuture(response.body().string());
        } catch (IOException ex) {
            return CompletableFutureUtils.failedFuture(ex);
        }
    }

}
