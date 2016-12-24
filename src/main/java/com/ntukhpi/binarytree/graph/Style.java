package com.ntukhpi.binarytree.graph;

/**
 * @author Alexander Gorbunov
 */
public enum Style {

    ANIMATION_BUTTON("animation-btn"),

    CELL_SELECTED_STYLE("cell-selected"),

    CELL_STYLE("cell"),

    CONSOLE_OUT_SELECTED("console-out-selected");

    private final String styleClass;

    Style(String style) {
        styleClass = style;
    }

    public String getStyleClass() {
        return styleClass;
    }
}
