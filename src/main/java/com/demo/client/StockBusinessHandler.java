package com.demo.client;

import java.util.concurrent.CompletableFuture;

public interface StockBusinessHandler {

    CompletableFuture<String> getPrice();

}
