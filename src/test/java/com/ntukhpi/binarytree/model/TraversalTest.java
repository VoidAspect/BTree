package com.ntukhpi.binarytree.model;

import org.junit.Test;

import static com.ntukhpi.binarytree.model.Traversal.*;
import static org.junit.Assert.*;

/**
 * @author Alexander Gorbunov
 */
public class TraversalTest {

    @Test
    public void testValues() {
        Traversal[] values = Traversal.values();

        assertEquals(values[0], PRE_ORDER);
        assertEquals(values[1], POST_ORDER);
        assertEquals(values[2], IN_ORDER);
    }
}