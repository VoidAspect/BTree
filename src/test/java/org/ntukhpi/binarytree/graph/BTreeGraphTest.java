package org.ntukhpi.binarytree.graph;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author ALexander Gorbunov
 */
public class BTreeGraphTest {

    private static Scene scene;

    private static BTreeGraph bTreeGraph;

    static { //workaround to access elements without launching application
        new JFXPanel();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        bTreeGraph = new BTreeGraph();
        scene = new Scene(bTreeGraph.getContent());
    }

    @Test
    public void testTreeGraphConsistency() throws Exception {
        assertEquals(2, bTreeGraph.getContent().getChildren().size());

        bTreeGraph.addNode(0);

        assertSelectedCell(0);
        assertEquals(1, getCellNumber());
        assertEquals(0, getVertexNumber());
        assertArrayEquals(new Integer[]{0}, bTreeGraph.getTraversalInOrder());
        assertArrayEquals(new Integer[]{0}, bTreeGraph.getTraversalPostOrder());
        assertArrayEquals(new Integer[]{0}, bTreeGraph.getTraversalPreOrder());

        bTreeGraph.addNode(100);

        assertSelectedCell(100);
        assertEquals(2, getCellNumber());
        assertEquals(1, getVertexNumber());
        assertArrayEquals(new Integer[]{0, 100}, bTreeGraph.getTraversalInOrder());
        assertArrayEquals(new Integer[]{0, 100}, bTreeGraph.getTraversalPostOrder());
        assertArrayEquals(new Integer[]{0, 100}, bTreeGraph.getTraversalPreOrder());

        bTreeGraph.addNode(-100);

        assertSelectedCell(-100);
        assertEquals(3, getCellNumber());
        assertEquals(2, getVertexNumber());
        assertArrayEquals(new Integer[]{-100, 0, 100}, bTreeGraph.getTraversalInOrder());
        assertArrayEquals(new Integer[]{0, 100, -100}, bTreeGraph.getTraversalPostOrder());
        assertArrayEquals(new Integer[]{0, -100, 100}, bTreeGraph.getTraversalPreOrder());

        bTreeGraph.mutateNode(-99);

        assertSelectedCell(-99);
        assertEquals(3, getCellNumber());
        assertEquals(2, getVertexNumber());
        assertArrayEquals(new Integer[]{-99, 0, 100}, bTreeGraph.getTraversalInOrder());
        assertArrayEquals(new Integer[]{0, 100, -99}, bTreeGraph.getTraversalPostOrder());
        assertArrayEquals(new Integer[]{0, -99, 100}, bTreeGraph.getTraversalPreOrder());

        bTreeGraph.max();
        assertSelectedCell(100);
        bTreeGraph.min();
        assertSelectedCell(-99);

        bTreeGraph.findNode(-100);
        assertSelectedCell(-99);

        bTreeGraph.findNode(0);
        assertSelectedCell(0);

        bTreeGraph.findNode(100);
        assertSelectedCell(100);

        bTreeGraph.findNode(-99);
        assertSelectedCell(-99);

        bTreeGraph.findNode(-99);
        assertSelectedCell(-99);

        bTreeGraph.removeNode();

        assertEquals(2, getCellNumber());
        assertEquals(1, getVertexNumber());
        assertNull(scene.lookup("#-99"));
        assertNull(scene.lookup(".cell-selected"));
        assertArrayEquals(new Integer[]{0, 100}, bTreeGraph.getTraversalInOrder());
        assertArrayEquals(new Integer[]{0, 100}, bTreeGraph.getTraversalPostOrder());
        assertArrayEquals(new Integer[]{0, 100}, bTreeGraph.getTraversalPreOrder());

        bTreeGraph.clear();

        assertEquals(0, getCellNumber());
        assertEquals(0, getVertexNumber());
        assertNull(scene.lookup(".cell-selected"));
        assertArrayEquals(new Integer[]{}, bTreeGraph.getTraversalInOrder());
        assertArrayEquals(new Integer[]{}, bTreeGraph.getTraversalPostOrder());
        assertArrayEquals(new Integer[]{}, bTreeGraph.getTraversalPreOrder());
    }

    @After
    public void clear() throws Exception {
        bTreeGraph.clear();
    }

    private int getVertexNumber() {
        return bTreeGraph.getContent().lookupAll(".vertex").size();
    }

    private int getCellNumber() {
        return bTreeGraph.getContent().lookupAll(".cell").size();
    }

    private void assertSelectedCell(int value) {
        assertEquals(value, Integer.parseInt(bTreeGraph.getContent().lookup(".cell-selected").getId()));
    }

}