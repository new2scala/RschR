package org.ditw.book.tinyWebJ;

public class LoggingFilter implements Filter {
    @Override
    public HttpRequest doFilter(HttpRequest req) {
        System.out.println("In logging filter - path: " + req.getPath());
        return req;
    }
}
