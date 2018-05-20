package org.ditw.book.tinyWebJ;

import java.util.*;

public class GreetingController extends TemplateController {

    private Random random;
    public GreetingController(View v) {
        super(v);
        this.random = new Random();
    }

    @Override
    protected Map<String, List<String>> doRequest(HttpRequest req) {
        Map<String, List<String>> helloModel = new HashMap<>();

        helloModel.put("greetings", generateGreetings(req.getBody()));
        return helloModel;
    }

    private List<String> generateGreetings(String nameCs) {
        String[] names = nameCs.split(",");
        List<String> greetings = new ArrayList<>(names.length);

        for (String n:names) {
            greetings.add(makeGreeting(n));
        }
        return greetings;
    }

    private static final String[] _greetings = new String[] {
        "Hello", "Greetings", "Salut", "Hola"
    };
    private String makeGreeting(String name) {
        String pref = _greetings[random.nextInt(_greetings.length)];
        return String.format("%s %s", pref, name);
    }
}
