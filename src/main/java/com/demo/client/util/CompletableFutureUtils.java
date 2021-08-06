package com.demo.client.util;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureUtils {

    public static <T> CompletableFuture<T> failedFuture(Exception ex) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(ex);
        return future;
    }

}
