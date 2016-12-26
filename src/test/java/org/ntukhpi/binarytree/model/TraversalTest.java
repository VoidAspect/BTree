package org.ntukhpi.binarytree.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Gorbunov
 */
public class TraversalTest {

    @Test
    public void testValues() {
        Traversal[] values = Traversal.values();

        Assert.assertEquals(values[0], Traversal.PRE_ORDER);
        Assert.assertEquals(values[1], Traversal.POST_ORDER);
        Assert.assertEquals(values[2], Traversal.IN_ORDER);
    }
}