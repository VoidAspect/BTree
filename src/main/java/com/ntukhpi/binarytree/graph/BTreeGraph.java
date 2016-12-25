package com.ntukhpi.binarytree.graph;

import com.ntukhpi.binarytree.model.NavigableTree;
import com.ntukhpi.binarytree.model.Traversal;
import com.ntukhpi.binarytree.model.Tree;
import com.ntukhpi.binarytree.model.TreeFactory;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Line;

import java.util.*;
import java.util.function.Predicate;

import static com.ntukhpi.binarytree.graph.Style.CELL_SELECTED_STYLE;
import static com.ntukhpi.binarytree.graph.Style.CELL_STYLE;

/**
 * Графическое представление бинарного дерева, основанное на элементах JavaFX.
 * <br>Может быть вставлен в любую внешнюю панель. Инкапсулирует все изменения модели данных.
 * <br>Состоит из ячеек ({@link BTreeGraph#cells}) и ребер ({@link BTreeGraph#vertices}),
 * доступ к которым реализован через общую группу ({@link BTreeGraph#content})
 * <br>Рендеринг контентов должен происходить при каждом изменении модели данных {@link BTreeGraph#tree}
 * <br>Обновления осуществляются синхронно.
 *
 * @author Alexander Gorbunov
 */
public class BTreeGraph {

    /**
     * Фабрика для создания экземпляров бинарного дерева.
     */
    private static final TreeFactory TREE_FACTORY = new TreeFactory();

    /**
     * Базовый радиус ячейки на графе. //todo автомасштаб
     */
    private static final double CELL_RADIUS = 20;

    private static final double VERTICAL_GAP = CELL_RADIUS * 3;

    /**
     * Ячейки - группа вершин дерева, предстваленных стилизованным {@link Label}.
     */
    private final Group cells;

    /**
     * Ячейки - группа ребер дерева, представленных {@link Line}.
     */
    private final Group vertices;

    /**
     * Общая группа визуальных компонентов, образованная наложением {@link BTreeGraph#cells} на {@link BTreeGraph#vertices}.
     */
    private final Group content;

    /**
     * Модель данных - структура из целочисленных значений в виде бинарного дерева.
     * Иммутабельная, персистентная, неавтосбалансированная реализация.
     * <br>При каждом изменении модели содержимое групп визуальных компонентов очищается и визуализация повторяется сначала.
     *
     * @see Tree
     */
    private Tree<Integer> tree;

    /**
     * Базовый конструктор - задает композицию групп визуальных компонентов.
     */
    public BTreeGraph() {
        cells = new Group();
        vertices = new Group();
        content = new Group(vertices, cells);
        tree = TREE_FACTORY.immutableTree();
    }

    /**
     * Метод очистки графа: очищает модель, визуальное предстваление и даже память!
     */
    public void clear() {
        tree = tree.clear();
        scrap();
        System.gc(); //не делайте этого дома без надзора взрослых
    }

    /**
     * Метод поиска вершины графа. Вызывает обновление визуальной части если на модели есть такая вершина.
     * <br>Обратите внимние, что поиск на визуальной части осуществляется отдельно с помощью css-селектора - для простоты и производительности.
     *
     * @param value искомое значение.
     * @return результат поиска: найден/не найден.
     */
    public boolean findNode(int value) {
        Optional.ofNullable(cells.lookup("#" + value))
            .ifPresent(label -> selectCell((Label) label));
        return tree.contains(value);
    }

    //todo COMMENTS FOR EVERYTHING
    public void addNode(int value) {
        tree = tree.insert(value);
        draw();
        findNode(value);
    }

    public void removeNode() {
        getSelected().ifPresent(selected -> removeNode(Integer.parseInt(selected.getText())));
    }

    public void mutateNode(int newValue) {
        getSelected().ifPresent(selected -> {
            int oldValue = Integer.parseInt(selected.getId());
            if (tree.contains(oldValue)) {
                tree = TREE_FACTORY.immutableTree(tree.traverse(Traversal.PRE_ORDER).stream()
                        .map(e -> (e == oldValue) ? newValue : e)
                        .toArray(Integer[]::new));
                draw();
            }
        });
    }

    public void balance() {
        List<Integer> traverse = tree.traverse(Traversal.IN_ORDER);
        tree = TREE_FACTORY.balancedTree(traverse.toArray(new Integer[traverse.size()]));
        draw();
    }

    public void max() {
        tree.max().ifPresent(this::findNode);
    }

    public void min() {
        tree.min().ifPresent(this::findNode);
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }

    public void unselect() {
        cells.getChildren()
                .forEach(node -> node.getStyleClass()
                        .removeIf(Predicate.isEqual(CELL_SELECTED_STYLE.getStyleClass())));
    }

    public Integer[] getTraversalInOrder() {
        return getTraversal(Traversal.IN_ORDER);
    }

    public Integer[] getTraversalPreOrder() {
        return getTraversal(Traversal.PRE_ORDER);
    }

    public Integer[] getTraversalPostOrder() {
        return getTraversal(Traversal.POST_ORDER);
    }

    private Integer[] getTraversal (Traversal traversal) {
       return tree.traverse(traversal).stream()
               .toArray(Integer[]::new);
    }

    private Optional<Label> getSelected() {
        return Optional.ofNullable((Label) cells.lookup('.' + CELL_SELECTED_STYLE.getStyleClass()));
    }

    private void removeNode(int value) {
        if (tree.contains(value)) {
            tree = tree.remove(value);
            draw();
        }
    }

    private void draw() {
        scrap();
        displayNodes((NavigableTree<Integer>) tree);
    }

    private void scrap() {
        cells.getChildren().clear();
        vertices.getChildren().clear();
    }

    private void displayNodes(NavigableTree<Integer> tree) {
        Map<Integer, Position> levels = buildLevels(tree, new Position(0, 0));

        levels.forEach((k, v) -> drawCell(k, v.x, v.y));
        drawVertices(tree);
    }

    private Map<Integer, Position> buildLevels(NavigableTree<Integer> tree, Position position) {
        if (tree.isEmpty()) return Collections.emptyMap();

        Map<Integer, Position> nodeMap = new HashMap<>();

        tree.getRoot().ifPresent(root -> nodeMap.put(root, position));

        NavigableTree<Integer> left = tree.left();
        NavigableTree<Integer> right = tree.right();

        int height = right.height() > left.height()? right.height() : left.height();
        double horizontalGap = (CELL_RADIUS) * Math.pow(2, height);

        if (!left.isEmpty()) nodeMap.putAll(buildLevels(left, position.move(-horizontalGap, VERTICAL_GAP)));

        if (!right.isEmpty()) nodeMap.putAll(buildLevels(right, position.move(horizontalGap, VERTICAL_GAP)));


        return nodeMap;
    }

    private void drawVertices(NavigableTree<Integer> tree) {
        Optional<Integer> root = tree.getRoot();

        if (root.isPresent()) {
            int rootVal = root.get();

            NavigableTree<Integer> leftBranch = tree.left();
            NavigableTree<Integer> rightBranch = tree.right();

            Optional<Integer> right = rightBranch.getRoot();
            Optional<Integer> left = leftBranch.getRoot();
            if (right.isPresent()) {
                drawVertice(rootVal, right.get());
                drawVertices(rightBranch);
            }
            if (left.isPresent()) {
                drawVertice(rootVal, left.get());
                drawVertices(leftBranch);
            }
        }

    }

    private void drawCell(int value, double x, double y) {
        String text = String.valueOf(value);
        Label cell = new Label(text);
        cell.setId(text);
        cell.setLayoutX(x - CELL_RADIUS);
        cell.setLayoutY(y - CELL_RADIUS);
        ObservableList<String> styleClass = cell.getStyleClass();
        styleClass.add(CELL_STYLE.getStyleClass());
        cell.setOnMouseClicked(e -> selectCell(cell, true));
        cell.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.DELETE)) {
                removeNode(value);
            }
        });
        cells.getChildren().add(cell);
    }

    private void drawVertice(int a, int b) {
        Label firstCell = (Label) cells.lookup("#" + a);
        if (firstCell == null) throw new IllegalStateException();

        Label secondCell = (Label) cells.lookup("#" + b);
        if (secondCell == null) throw new IllegalStateException();

        Line vertice = new Line();
        vertice.startXProperty().bind(firstCell.layoutXProperty().add(CELL_RADIUS));
        vertice.startYProperty().bind(firstCell.layoutYProperty().add(CELL_RADIUS));
        vertice.endXProperty().bind(secondCell.layoutXProperty().add(CELL_RADIUS));
        vertice.endYProperty().bind(secondCell.layoutYProperty().add(CELL_RADIUS));
        vertices.getChildren().add(vertice);
    }

    private void selectCell(Label cell) {
        selectCell(cell, false);
    }

    private void selectCell(Label cell, boolean unselectable) {
        ObservableList<String> styleClass = cell.getStyleClass();
        String selected = CELL_SELECTED_STYLE.getStyleClass();
        if (!styleClass.contains(selected)) {
            getSelected().ifPresent(label -> label.getStyleClass().remove(selected));
            styleClass.add(selected);
        } else if (unselectable) {
            styleClass.remove(selected);
        }
    }

    public Group getContent() {
        return content;
    }

    private class Position {

        private final double x;

        private final double y;

        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        Position move(double x, double y) {
            return new Position(this.x + x, this.y + y);
        }
    }
}
