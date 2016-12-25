package com.ntukhpi.binarytree.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Alexander Gorbunov
 */
public class TreeTest {

    private static final TreeFactory FACTORY = new TreeFactory();

    @Test
    public void testTraversal() throws Exception {
        Integer[] elements = {5, 2, 7, 3, 1, 8, 6};
        System.out.println("initial:\t" + Arrays.toString(elements));

        Tree<Integer> binaryTree = FACTORY.immutableTree(elements);
        System.out.println("bi-tree:\t" + binaryTree);

        Object[] inOrder = binaryTree.traverse(Traversal.IN_ORDER).toArray();
        System.out.println("in-order:\t" + Arrays.toString(inOrder));

        Object[] preOrder = binaryTree.traverse(Traversal.PRE_ORDER).toArray();
        System.out.println("pre-order:\t" + Arrays.toString(preOrder));

        Object[] postOrder = binaryTree.traverse(Traversal.POST_ORDER).toArray();
        System.out.println("post-order:\t" + Arrays.toString(postOrder));

        Optional<Integer> oMax = binaryTree.max();
        int max = oMax.orElseGet(() -> {
            fail();
            return null;
        });
        assertEquals(8, max);
        int min = binaryTree.min().orElseGet(() -> {
            fail();
            return null;
        });
        assertEquals(1, min);

        assertArrayEquals(new Integer[]{1, 2, 3, 5, 6, 7, 8}, inOrder);

        assertArrayEquals(new Integer[]{5, 2, 1, 3, 7, 6, 8}, preOrder);

        assertArrayEquals(new Integer[]{5, 7, 8, 6, 2, 3, 1}, postOrder);
    }

    /**
     * Test null behavior
     *
     * @throws Exception any checked exception is rethrown
     */
    @Test
    public void testNull() throws Exception {
        Tree<Integer> tree = FACTORY.immutableTree(1, 2, 3);


        assertEquals(tree, tree.insert(null));
        assertEquals(tree, tree.remove(null));
        assertFalse(tree.contains(null));

        String eMessage = "This node shouldn't have null value! Value passed for check: " + null + "; tree structure: (_ null _)";
        try {
            new Leaf<>(null);
            fail();
        } catch (TreeNodeValueException e) {
            assertEquals(e.getMessage(), eMessage);
        }
    }

    @Test
    public void testNavigation() {
        NavigableTree<Integer> binaryTree = FACTORY.immutableTree(5, 2, 7, 3, 1, 8, 6);

        NavigableTree<Integer> right = binaryTree.right();

        NavigableTree<Integer> left = binaryTree.left();

        assertFalse(right.isEmpty());
        assertFalse(right.right().isEmpty());
        assertTrue(right.right().right().isEmpty());
        assertArrayEquals(new Integer[]{7, 6, 8}, right.traverse(Traversal.PRE_ORDER).toArray());

        assertFalse(left.isEmpty());
        assertArrayEquals(new Integer[]{2, 1, 3}, left.traverse(Traversal.PRE_ORDER).toArray());
    }

    @Test
    public void testEmpty() throws Exception {
        Tree<Integer> empty = FACTORY.immutableTree();
        assertFalse(empty.max().isPresent());
        assertFalse(empty.min().isPresent());
        assertTrue(empty.isEmpty());
        assertTrue(empty.traverse(Traversal.POST_ORDER).isEmpty());
        assertTrue(empty.traverse(Traversal.PRE_ORDER).isEmpty());
        assertTrue(empty.traverse(Traversal.IN_ORDER).isEmpty());
        assertFalse(empty.contains(0));
        assertEquals(empty.remove(1), empty);
    }


    @Test
    public void testEquals() throws Exception {
        Tree<Integer> empty = FACTORY.immutableTree();
        Tree<Integer> nonEmpty = FACTORY.immutableTree(1, 2, 3);
        Tree<Integer> populated = empty.insert(1).insert(2).insert(3);

        assertEquals(populated, nonEmpty);
        assertNotEquals(populated, FACTORY.immutableTree(5, 4, 7));
        assertEquals(nonEmpty.traverse(Traversal.IN_ORDER),
                populated.traverse(Traversal.IN_ORDER));
    }

    @Test
    public void testInsert() throws Exception {
        assertEquals(FACTORY.immutableTree(2, 1), FACTORY.immutableTree(2).insert(1));
    }

    @Test
    public void testRemove() throws Exception {
        ImmutableBinaryTree<Integer> tree = FACTORY.immutableTree(2, 1, 3);
        assertEquals(FACTORY.immutableTree(2, 1), tree.remove(3));
        assertEquals(FACTORY.immutableTree(), tree.clear());

        assertTreeStructureAfterRootRemoval(
                "(((_ 4 _) 5 _) 6 (_ 9 _))",
                FACTORY.immutableTree(7, 5, 6, 4, 9));

        assertTreeStructureAfterRootRemoval("((_ 5 _) 8 (_ 10 (_ 12 _)))",
                FACTORY.immutableTree(7, 10, 8, 12, 5));

        tree = FACTORY.immutableTree(10, 9, 8, 7, 12, 41);

        while (!tree.isEmpty()) { //test for root-cutting fix
            tree = tree.cut();
            System.out.println("STRUCTURE: " + tree);
            System.out.println("CLASS:     " + tree.getClass().getName());
        }

    }

    private void assertTreeStructureAfterRootRemoval(String structure, ImmutableBinaryTree<Integer> tree) {
        Optional<Integer> treeRoot = tree.getRoot();
        if (treeRoot.isPresent()) {
            int root = treeRoot.get();
            System.out.println("before root remove: " + tree.toString());
            String actual = tree.remove(root).toString();
            System.out.println("after root remove:  " + actual);
            assertEquals(structure, actual);
        } else if (!tree.isEmpty()) {
            fail("Null value in non-empty tree");
        }
    }

    @Test
    public void testBalance() {
        Integer[] vals = {5, 4, 3, 2, 1, 6, 7, 8, 9};
        ImmutableBinaryTree<Integer> tree = FACTORY.immutableTree(vals);

        Object[] preOrder = tree.traverse(Traversal.PRE_ORDER).toArray();
        assertArrayEquals(vals, preOrder);

        ImmutableBinaryTree<Integer> balancedTree = FACTORY.balancedTree(vals);

        String printedTree = balancedTree.toString();
        System.out.println(printedTree);
        assertEquals("((((_ 1 _) 2 _) 3 (_ 4 _)) 5 (((_ 6 _) 7 _) 8 (_ 9 _)))", printedTree);
    }

    @Test
    public void levelTest() {
        assertEquals(5, FACTORY.immutableTree(1, 2, 3, 4, 5).height());
        assertEquals(3, FACTORY.immutableTree(5, 3, 7, 2, 4).height());
        assertEquals(2, FACTORY.balancedTree(3, 2, 1).height());
        assertEquals(1, FACTORY.balancedTree(1).height());
        assertEquals(0, FACTORY.immutableTree().height());
        ImmutableBinaryTree<Integer> tree = FACTORY.immutableTree(84, 12, -14, -972, 44, 32, 45, 56, 374, 321, 132, 906);
        System.out.println(tree.toString());
        assertEquals(5, tree.height());
    }

}