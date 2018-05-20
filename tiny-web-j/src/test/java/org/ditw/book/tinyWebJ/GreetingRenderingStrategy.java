package org.ditw.book.tinyWebJ;

import java.util.List;
import java.util.Map;

public class GreetingRenderingStrategy implements RenderingStrategy {

    @Override
    public String renderView(Map<String, List<String>> model) {
        List<String> greetings = model.get("greetings");

        StringBuilder respBody = new StringBuilder();
        respBody.append("<h1>Greetings</h1>\n");

        for (String grt:greetings) {
            respBody.append(
                String.format("<h2>%s</h2>\n", grt)
            );
        }
        return respBody.toString();
    }
}
