package com.atypon.connect_task;

public class UrlBuilder {
  private String scheme;
  private String host;
  private String path;

  private UrlBuilder() {
  }

  public static UrlBuilder builder() {
    return new UrlBuilder();
  }

  public UrlBuilder scheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  public UrlBuilder host(String host) {
    this.host = host;
    return this;
  }

  public UrlBuilder path(String path) {
    this.path = path;
    return this;
  }

  public String build() {
    return scheme + "://" + host + path;
  }
}

