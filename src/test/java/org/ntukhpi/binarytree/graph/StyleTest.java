package org.ntukhpi.binarytree.graph;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Alexander Gorbunov
 */
public class StyleTest {

    @Test
    public void testStyles() throws Exception{
        Style[] values = Style.values();

        Assert.assertEquals(values[0], Style.ANIMATION_BUTTON);
        assertStyleClass("animation-btn", Style.ANIMATION_BUTTON);

        Assert.assertEquals(values[1], Style.VERTEX);
        assertStyleClass("vertex", Style.VERTEX);

        Assert.assertEquals(values[2], Style.CELL_STYLE);
        assertStyleClass("cell", Style.CELL_STYLE);

        Assert.assertEquals(values[3], Style.CELL_SELECTED_STYLE);
        assertStyleClass("cell-selected", Style.CELL_SELECTED_STYLE);

        Assert.assertEquals(values[4], Style.CONSOLE_OUT);
        assertStyleClass("console-out", Style.CONSOLE_OUT);

        Assert.assertEquals(values[5], Style.CONSOLE_OUT_ARROW);
        assertStyleClass("console-out-arrow", Style.CONSOLE_OUT_ARROW);

        Assert.assertEquals(values[6], Style.CONSOLE_OUT_VALUE);
        assertStyleClass("console-out-value", Style.CONSOLE_OUT_VALUE);

        Assert.assertEquals(values[7], Style.CONSOLE_OUT_SELECTED);
        assertStyleClass("console-out-selected", Style.CONSOLE_OUT_SELECTED);
    }

    private void assertStyleClass(String styleClass, Style style) {
        assertEquals(styleClass, style.getStyleClass());
    }

}