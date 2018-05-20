package org.ditw.book.tinyWebJ;

import java.net.HttpRetryException;

public class GreetingControllerTest {

    public static void main(String[] args) {
        StrategyView view = new StrategyView(new GreetingRenderingStrategy());
        GreetingController controller = new GreetingController(view);

        HttpRequest req = HttpRequest.Builder.newBuilder()
            .body("John,James, Amy")
            .build();
        HttpResponse resp = controller.handleRequest(
            req
        );
        System.out.println(resp.getBody());
    }
}
