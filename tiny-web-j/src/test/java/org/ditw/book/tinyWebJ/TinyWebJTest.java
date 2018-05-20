package org.ditw.book.tinyWebJ;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TinyWebJTest {

    public static void main(String[] args) {
        Map<String, Controller> controllerMap = new HashMap<>();
        controllerMap.put("greetings",
            new GreetingController(new StrategyView(new GreetingRenderingStrategy()))
        );
        List<Filter> filterList =  new LinkedList<>();
        filterList.add(new LoggingFilter());
        TinyWebJ twj = new TinyWebJ(
            controllerMap, filterList
        );
//        HttpRequest req = HttpRequest.Builder.newBuilder()
//            .path("greetings1")
//            .body("John,James, Amy")
//            .build();
        HttpRequest req = HttpRequest.Builder.newBuilder()
            .path("greetings")
            .body("John,James, Amy")
            .build();
        HttpResponse resp = twj.handleRequest(req);
        System.out.println(resp.getBody());
    }
}
