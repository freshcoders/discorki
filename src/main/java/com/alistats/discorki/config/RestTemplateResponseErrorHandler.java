package com.alistats.discorki.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestTemplateResponseErrorHandler
    implements ResponseErrorHandler {

  final Logger LOG = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

  @Override
  public boolean hasError(ClientHttpResponse httpResponse)
      throws IOException {

    return httpResponse.getStatusCode().isError();
  }

  @Override
  public void handleError(ClientHttpResponse httpResponse)
      throws IOException {

    switch (httpResponse.getStatusCode().value()) {
      case 403, 401 -> {
        LOG.error("API key is rejected, did it expire?");
        System.exit(1);
      }
      default -> throw new HttpClientErrorException(httpResponse.getStatusCode());
    }
  }
}