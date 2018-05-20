package org.ditw.book.tinyWebJ;

public class HttpResponse {
    public static class Builder {
        private String body;
        private Integer responseCode;

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder responseCode(Integer responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

        public static Builder newBuilder() {
            return new Builder();
        }
    }

    private final String body;

    public Integer getResponseCode() {
        return responseCode;
    }

    private final Integer responseCode;

    public String getBody() {
        return body;
    }

    private HttpResponse(Builder builder) {
        this.body = builder.body;
        this.responseCode = builder.responseCode;
    }

}