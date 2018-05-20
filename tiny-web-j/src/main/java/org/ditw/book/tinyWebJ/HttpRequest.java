package org.ditw.book.tinyWebJ;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public static class Builder {
        private String body;
        private String path;

        private Map<String,String> headers = new HashMap<>();
        private Builder() {

        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }
        public Builder path(String path) {
            this.path = path;
            return this;
        }
        public Builder addHeader(String k, String v) {
            headers.put(k, v);
            return this;
        }

        public Builder builderFrom(HttpRequest req) {
            Builder builder = new Builder()
                .body(req.body)
                .path(req.path);

            for (String k : req.getHeaders().keySet()) {
                builder.addHeader(k, req.getHeaders().get(k));
            }

            return builder;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }


    private final Map<String, String> headers;

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getPath() {
        return path;
    }

    private final String body;
    private final String path;

    public HttpRequest(Builder builder) {
        body = builder.body;
        path = builder.path;
        headers = builder.headers;
    }

}