package org.ditw.book.tinyWebJ;

import java.util.List;
import java.util.Map;

public class StrategyView implements View {
    private RenderingStrategy viewRenderer;

    public StrategyView(RenderingStrategy renderer) {
        this.viewRenderer = renderer;
    }

    @Override
    public String render(Map<String, List<String>> model) throws RenderingException {
        try {
            return viewRenderer.renderView(model);
        }
        catch (Exception e) {
            throw new RenderingException(e);
        }
    }
}