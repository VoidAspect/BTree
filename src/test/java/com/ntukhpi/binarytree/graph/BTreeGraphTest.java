package com.ntukhpi.binarytree.graph;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ALexander Gorbunov
 */
public class BTreeGraphTest {

    @Test
    public void testTreeGraphConsistency() {
        BTreeGraph bTreeGraph = new BTreeGraph();
        assertEquals(2, bTreeGraph.getContent().getChildren().size());
    }

}