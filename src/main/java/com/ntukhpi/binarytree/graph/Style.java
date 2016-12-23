package com.ntukhpi.binarytree.graph;

/**
 * @author Alexander Gorbunov
 */
public enum Style {

    CELL_SELECTED_STYLE("cell-selected"),

    CELL_STYLE("cell");

    private final String styleClass;

    Style(String style) {
        styleClass = style;
    }

    public String getStyleClass() {
        return styleClass;
    }
}
