package com.example.batchdemo.util;

import com.example.batchdemo.exception.GetItemFailException;
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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

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

    private static void setHardTimeout(HttpRequestBase methodObject, int hardTimeout){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (methodObject != null) {
                    methodObject.abort();
                }
            }
        };
        new Timer(true).schedule(task, hardTimeout * 1000);
    }


    public static String getResponseText(HttpRequestBase httpMethod) throws Not200Exception, IOException, GetItemFailException {
        BufferedReader reader = null;
        CloseableHttpClient client = null;
        StringBuffer response = new StringBuffer();

        try{
            //setHardTimeout(httpMethod,10);
            client = getClientWithTimeout(120);
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
        }catch(SocketException | SocketTimeoutException e){
            log.error(e.toString());
            throw new GetItemFailException(e.getMessage(),e);
        }finally {
            if(client != null ) client.close();
            if(reader != null ) reader.close();
        }
        log.debug("response : {}",response.toString());
        return response.toString();
    }
}
