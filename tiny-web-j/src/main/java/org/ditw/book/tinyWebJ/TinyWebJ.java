package org.ditw.book.tinyWebJ;

import java.util.List;
import java.util.Map;

public class TinyWebJ {

    private Map<String, Controller> controllers;

    private List<Filter> filters;

    public TinyWebJ(Map<String, Controller> controllers, List<Filter> filters) {
        this.controllers = controllers;
        this.filters = filters;
    }

    public HttpResponse handleRequest(HttpRequest req) {
        HttpRequest currReq = req;

        for (Filter filter : filters) {
            currReq = filter.doFilter(currReq);
        }

        Controller controller = controllers.get(currReq.getPath());
        if (null == controller)
            return null;
        return controller.handleRequest(currReq);
    }
}
