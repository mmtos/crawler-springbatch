package com.example.batchdemo.util;

import com.example.batchdemo.exception.Not200Exception;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpConnectUtil {
    static Logger log = LoggerFactory.getLogger(HttpConnectUtil.class);

    private static CloseableHttpClient getDefaultClient(){
        return HttpClients.createDefault();
    }

    private static CloseableHttpClient getClientWithTimeout(int timeout){
        // timeout 설정 :  https://www.baeldung.com/httpclient-timeout
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }



    public static String getResponseText(HttpRequestBase httpMethod) throws Not200Exception, IOException {
        BufferedReader reader = null;
        CloseableHttpClient client = null;
        StringBuffer response = new StringBuffer();

        try{
            client = getClientWithTimeout(20);
            CloseableHttpResponse httpResponse = client.execute(httpMethod);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(!(statusCode >= 200 && statusCode<=300)){
                throw new Not200Exception("정상응답이 아닙니다. 응답코드 : " + String.valueOf(statusCode));
            }
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
        }finally {
            if(client != null ) client.close();
            if(reader != null ) reader.close();
        }
        log.debug("response : {}",response.toString());
        return response.toString();
    }
}
