package com.demo.client;

import com.demo.client.util.CompletableFutureUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.okhttp.MediaType;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.demo.client.util.JsonUtils.MAPPER;

@Log4j2
public class ApacheSyncStockBusinessHandler implements StockBusinessHandler {

   private String priceUrl;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final Random rand = new Random();
    private final CloseableHttpClient client;
    private final PoolingHttpClientConnectionManager connectionManager;
    private final RequestConfig requestConfig;

    public ApacheSyncStockBusinessHandler(HttpClientConfiguration config) {
        this.priceUrl = config.getServerGetUrl();
        connectionManager = new PoolingHttpClientConnectionManager();
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getConnectTimeOut(), TimeUnit.MILLISECONDS)
                .setDefaultKeepAlive(config.getKeepAliveTime(), TimeUnit.MILLISECONDS)
                .setResponseTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
        connectionManager.setMaxTotal(config.getMaxConnection());
        client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

    }

    @Override
    public CompletableFuture<String> getPrice() {
        log.info("Call from Apache sync");
        final HttpGet httpGet = new HttpGet(priceUrl);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        try (final CloseableHttpResponse response = client.execute(httpGet)) {
            HttpEntity httpEntity = response.getEntity();
            if (Objects.isNull(httpEntity)) {
                return CompletableFutureUtils.failedFuture(new Exception("failed request"));
            }
            return CompletableFuture.completedFuture(EntityUtils.toString(httpEntity));
        } catch (IOException | ParseException ex) {
            return CompletableFutureUtils.failedFuture(ex);
        }
    }


}
