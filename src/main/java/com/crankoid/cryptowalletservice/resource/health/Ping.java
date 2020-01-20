package com.crankoid.cryptowalletservice.resource.health;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/")
public class Ping {

    @GetMapping()
    public String healthCheck() {
        return "OK";
    }

    @Scheduled(fixedDelay = 15L * 60L * 1000L) //Every 15 minutes
    private void keepAlive() {
        try {
            ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory()
                    .createRequest(new URI("cryptowalletservice.herokuapp.com/"), HttpMethod.GET);
            ClientHttpResponse respone = request.execute();
            System.out.println("Keep alive done, status code is '" + respone.getStatusCode() + "'");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }

}
