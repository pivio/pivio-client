package io.pivio.upload;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;


class RestCallErrorHandler implements ResponseErrorHandler {
  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return false;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
  }
}
