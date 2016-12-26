package com.ntukhpi.binarytree.graph;

import org.junit.Test;

import static com.ntukhpi.binarytree.graph.Style.*;
import static org.junit.Assert.*;

/**
 * @author Alexander Gorbunov
 */
public class StyleTest {

    @Test
    public void testStyles() throws Exception{
        Style[] values = Style.values();

        assertEquals(values[0], ANIMATION_BUTTON);
        assertStyleClass("animation-btn", ANIMATION_BUTTON);

        assertEquals(values[1], VERTEX);
        assertStyleClass("vertex", VERTEX);

        assertEquals(values[2], CELL_STYLE);
        assertStyleClass("cell", CELL_STYLE);

        assertEquals(values[3], CELL_SELECTED_STYLE);
        assertStyleClass("cell-selected", CELL_SELECTED_STYLE);

        assertEquals(values[4], CONSOLE_OUT_SELECTED);
        assertStyleClass("console-out-selected", CONSOLE_OUT_SELECTED);
    }

    private void assertStyleClass(String styleClass, Style style) {
        assertEquals(styleClass, style.getStyleClass());
    }

}