package org.ditw.book.tinyWebJ;

import java.util.List;
import java.util.Map;

public abstract class TemplateController implements Controller {

    private View view;

    public TemplateController(View view) {
        this.view = view;
    }

    protected abstract Map<String, List<String>> doRequest(HttpRequest req);

    @Override
    public HttpResponse handleRequest(HttpRequest req) {
        Integer responseCode = 200;
        String responseBody = "";

        try {
            Map<String, List<String>> model = doRequest(req);
            responseBody = view.render(model);
        }
        catch (RenderingException re) {
            responseCode = 500;
        }
        catch (Exception e) {
            responseCode = 500;
        }

        return HttpResponse.Builder.newBuilder()
            .body(responseBody)
            .responseCode(responseCode)
            .build();
    }
}