package com.walmart.aex.sp.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Configuration
@Slf4j
public class RestTemplateConfig {

   @Bean
   public RestTemplate restTemplate() {
      try {
         TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
         SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
         SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
         CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
         HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
         requestFactory.setHttpClient(httpClient);
         return new RestTemplate(requestFactory);

      } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
         log.error("Error Occurred in Rest Template Configuration - " + Arrays.toString(e.getStackTrace()));
         return null;
      }
   }
}
