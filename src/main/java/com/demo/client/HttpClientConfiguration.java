package com.demo.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HttpClientConfiguration {


    private ClientType clientType;
    private int maxConnection;
    private long keepAliveTime;
    private long connectTimeOut;
    private long readTimeout;
    private long writeTimeout;
    private String serverGetUrl;

    public enum ClientType {
        OKHTTP_SYNC,
        OKHTTP_ASYNC,
        APACHE_SYNC,
        APACHE_ASYNC,
    }

}

