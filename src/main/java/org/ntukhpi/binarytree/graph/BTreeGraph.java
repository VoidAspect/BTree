package org.ntukhpi.binarytree.graph;

import org.ntukhpi.binarytree.model.NavigableTree;
import org.ntukhpi.binarytree.model.Traversal;
import org.ntukhpi.binarytree.model.Tree;
import org.ntukhpi.binarytree.model.TreeFactory;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Line;

import java.util.*;
import java.util.function.Predicate;

/**
 * Графическое представление бинарного дерева, основанное на элементах JavaFX.
 * <br>Может быть вставлен в любую внешнюю панель. Инкапсулирует все изменения модели данных.
 * <br>Состоит из ячеек ({@link BTreeGraph#cells}) и ребер ({@link BTreeGraph#vertexes}),
 * доступ к которым реализован через общую группу ({@link BTreeGraph#content})
 * <br>Рендеринг контента должен происходить при каждом изменении модели данных {@link BTreeGraph#tree}
 * <br>Обновления осуществляются синхронно.
 * <br>Далее, дабы избежать путаницы, в документации этого класса называю модель данных "деревом", а визуальную модель - "графом".
 *
 * @author Alexander Gorbunov
 */
public class BTreeGraph {

    /**
     * Фабрика для создания экземпляров бинарного дерева.
     */
    private static final TreeFactory TREE_FACTORY = new TreeFactory();

    /**
     * Базовый радиус ячейки на графе.
     */
    public static final double CELL_RADIUS = 20;

    /**
     * Вертикальное расстояние между вершинами графа.
     */
    private static final double VERTICAL_GAP = CELL_RADIUS * 3;

    /**
     * Ячейки - группа вершин графа, предстваленных стилизованным {@link Label}.
     */
    private final Group cells;

    /**
     * Ребра - группа ребер графа, представленных {@link Line}.
     */
    private final Group vertexes;

    /**
     * Общая группа визуальных компонентов, образованная наложением {@link BTreeGraph#cells} на {@link BTreeGraph#vertexes}.
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
        vertexes = new Group();
        content = new Group(vertexes, cells);
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

    /**
     * Добавить значение на дерево, перерисовать граф и выделить добавленную вершину.
     *
     * @param value целое число.
     */
    public void addNode(int value) {
        tree = tree.insert(value);
        draw();
        findNode(value);
    }

    /**
     * Удалить с графа вершину, которая выделена на данный момент.
     */
    public void removeNode() {
        getSelected().ifPresent(selected -> removeNode(Integer.parseInt(selected.getText())));
    }

    /**
     * Изменить значение выделенной вершины графа,
     * перестроить бинарное дерево и выделить измененную ячейку.
     *
     * @param newValue новое значение.
     */
    public void mutateNode(int newValue) {
        getSelected().ifPresent(selected -> {
            int oldValue = Integer.parseInt(selected.getId());
            if (tree.contains(oldValue)) {
                tree = TREE_FACTORY.immutableTree(tree.traverse(Traversal.PRE_ORDER).stream()
                        .map(e -> (e == oldValue) ? newValue : e)
                        .toArray(Integer[]::new));
                draw();
                findNode(newValue);
            }
        });
    }

    /**
     * Перестроить дерево в сбалансированное и перерисовать граф.
     */
    public void balance() {
        List<Integer> traverse = tree.traverse(Traversal.IN_ORDER);
        tree = TREE_FACTORY.balancedTree(traverse.toArray(new Integer[traverse.size()]));
        draw();
    }

    /**
     * Найти максимальное значение в дереве и выделить его на графе.
     */
    public void max() {
        tree.max().ifPresent(this::findNode);
    }

    /**
     * Найти минимальное значение в дереве и выделить его на графе.
     */
    public void min() {
        tree.min().ifPresent(this::findNode);
    }

    /**
     * Проверка, является ли дерево пустым.
     *
     * @return результат проверки
     */
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    /**
     * Убрать выделение с выделенной вершины графа.
     */
    public void unselect() {
        cells.getChildren()
                .forEach(node -> node.getStyleClass()
                        .removeIf(Predicate.isEqual(Style.CELL_SELECTED_STYLE.getStyleClass())));
    }

    /**
     * Получить элементы дерева во внутреннем порядке обхода.
     *
     * @return массив цклых чисел.
     */
    public Integer[] getTraversalInOrder() {
        return getTraversal(Traversal.IN_ORDER);
    }

    /**
     * Получить элементы дерева в прямом порядке обхода.
     *
     * @return массив цклых чисел.
     */
    public Integer[] getTraversalPreOrder() {
        return getTraversal(Traversal.PRE_ORDER);
    }

    /**
     * Получить элементы дерева в обратном порядке обхода.
     *
     * @return массив цклых чисел.
     */
    public Integer[] getTraversalPostOrder() {
        return getTraversal(Traversal.POST_ORDER);
    }

    /**
     * Получить элементы дерева в указанном порядке обзода.
     * <br>{@code private} метод, вынесенный во избежание повторений кода.
     *
     * @param traversal {@link Traversal}.
     * @return массив целых чисел.
     */
    private Integer[] getTraversal(Traversal traversal) {
        return tree.traverse(traversal).stream()
                .toArray(Integer[]::new);
    }

    /**
     * Получить вершину графа, выделенную на данный момент.
     * Если таковой нет, {@link Optional} должен хранить пустую ссылку (null).
     *
     * @return контейнер, который может содержать ссылку на выделенную ячейку.
     */
    public Optional<Label> getSelected() {
        return Optional.ofNullable((Label) cells.lookup('.' + Style.CELL_SELECTED_STYLE.getStyleClass()));
    }

    /**
     * Удалить элемент с данным значением с дерева и перерисовать граф.
     *
     * @param value целое число.
     */
    private void removeNode(int value) {
        if (tree.contains(value)) {
            tree = tree.remove(value);
            draw();
        }
    }

    /**
     * Метод перерисовки графа.
     */
    private void draw() {
        scrap();
        display((NavigableTree<Integer>) tree);
    }

    /**
     * Метод очистки графа - удаляет все вершины и ребра.
     */
    private void scrap() {
        cells.getChildren().clear();
        vertexes.getChildren().clear();
    }

    /**
     * Метод отрисовки дерева.
     *
     * @param tree модель данных, на основе которой выполняется построение графа.
     */
    private void display(NavigableTree<Integer> tree) {
        Map<Integer, Position> levels = buildLevels(tree, new Position(0, 0));

        levels.forEach(this::drawCell);
        drawVertexes(tree);
    }

    /**
     * Метод построения карты позиций вершин графа на 2d системе координат.
     * <br>Горизонтальное расстояние между вершинами одного уровня
     * считается как 2^[высота поддерева] * {@link BTreeGraph#CELL_RADIUS}
     * <br>Вертикальное расстояние между вершинами соседних уровней = {@link BTreeGraph#VERTICAL_GAP}
     * <br>Каждая запись в итоговой карте соответствует значению из дерева
     * и его позиции при отрисовке веришины графа на системе координат.
     * <br>Записи добавляются в ходе рекурсивного обхода дерева.
     *
     * @param tree     {@link NavigableTree}
     * @param position {@link Position}
     * @return карта позиций для каждого значения из дерева.
     */
    private Map<Integer, Position> buildLevels(NavigableTree<Integer> tree, Position position) {
        if (tree.isEmpty()) return Collections.emptyMap();

        Map<Integer, Position> nodeMap = new HashMap<>();

        tree.getRoot().ifPresent(root -> nodeMap.put(root, position));

        NavigableTree<Integer> left = tree.left();
        NavigableTree<Integer> right = tree.right();

        int height = right.height() > left.height() ? right.height() : left.height();
        double horizontalGap = (CELL_RADIUS) * Math.pow(2, height);

        if (!left.isEmpty()) nodeMap.putAll(buildLevels(left, position.move(-horizontalGap, VERTICAL_GAP)));

        if (!right.isEmpty()) nodeMap.putAll(buildLevels(right, position.move(horizontalGap, VERTICAL_GAP)));


        return nodeMap;
    }

    /**
     * Метод отрисовки ребер графа по модели данных.
     * <br>Вызывется после отрисовки вершин графа.
     * <br>В качестве модели связности использует {@link NavigableTree}.
     * <br>Ребра добавляются в ходе рекурсивного обхода дерева.
     *
     * @param tree {@link NavigableTree}
     */
    private void drawVertexes(NavigableTree<Integer> tree) {
        Optional<Integer> root = tree.getRoot();

        if (root.isPresent()) {
            int rootVal = root.get();

            NavigableTree<Integer> leftBranch = tree.left();
            NavigableTree<Integer> rightBranch = tree.right();

            Optional<Integer> right = rightBranch.getRoot();
            Optional<Integer> left = leftBranch.getRoot();
            if (right.isPresent()) {
                drawVertex(rootVal, right.get());
                drawVertexes(rightBranch);
            }
            if (left.isPresent()) {
                drawVertex(rootVal, left.get());
                drawVertexes(leftBranch);
            }
        }

    }

    /**
     * Метод отрисовки вершины графа в указанной позиции.
     *
     * @param value    значение в новой вершине.
     * @param position позиция новой вершины.
     * @see Position
     */
    private void drawCell(int value, Position position) {
        String text = String.valueOf(value);
        Label cell = new Label(text);
        cell.setId(text);
        cell.setLayoutX(position.x - CELL_RADIUS);
        cell.setLayoutY(position.y - CELL_RADIUS);
        cell.getStyleClass().add(Style.CELL_STYLE.getStyleClass());
        cell.setOnMouseClicked(e -> {
            String selected = Style.CELL_SELECTED_STYLE.getStyleClass();
            ObservableList<String> styleClass = cell.getStyleClass();
            if (!styleClass.contains(selected)) {
                getSelected().ifPresent(label -> label.getStyleClass().remove(selected));
                styleClass.add(selected);
            } else {
                styleClass.remove(selected);
            }
        });
        cell.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.DELETE)) {
                removeNode(value);
            }
        });
        cells.getChildren().add(cell);
    }

    /**
     * Метод отрисовки ребра графа между двумя его вершинами с указанными значениями.
     *
     * @param start значение в первой вершине
     * @param end   значение во второй вершине.
     */
    private void drawVertex(int start, int end) {
        Label firstCell = (Label) cells.lookup("#" + start);
        if (firstCell == null) throw new IllegalStateException();

        Label secondCell = (Label) cells.lookup("#" + end);
        if (secondCell == null) throw new IllegalStateException();

        Line vertex = new Line();
        vertex.getStyleClass().add(Style.VERTEX.getStyleClass());
        vertex.startXProperty().bind(firstCell.layoutXProperty().add(CELL_RADIUS));
        vertex.startYProperty().bind(firstCell.layoutYProperty().add(CELL_RADIUS));
        vertex.endXProperty().bind(secondCell.layoutXProperty().add(CELL_RADIUS));
        vertex.endYProperty().bind(secondCell.layoutYProperty().add(CELL_RADIUS));
        vertexes.getChildren().add(vertex);
    }

    /**
     * Метод выделения вершины.
     *
     * @param cell ячейка для выделения.
     */
    private void selectCell(Label cell) {
        ObservableList<String> styleClass = cell.getStyleClass();
        String selected = Style.CELL_SELECTED_STYLE.getStyleClass();
        if (!styleClass.contains(selected)) {
            getSelected().ifPresent(label -> label.getStyleClass().remove(selected));
            styleClass.add(selected);
        }
    }

    /**
     * Геттер для группы визуальных компнентов графа.
     *
     * @return {@link BTreeGraph#content}
     */
    public Group getContent() {
        return content;
    }

    /**
     * Внутренний класс, определяющих 2d координаты точки.
     * Обьекты этого класса иммутабельны.
     *
     * @author Alexander Gorbunov
     */
    private class Position {

        /**
         * Абсцисса точки.
         */
        private final double x;

        /**
         * Ордината точки.
         */
        private final double y;

        /**
         * Конструктор объектов класса {@code Position}.
         */
        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Метод получения новой позиции,
         * полученной в результате смещения от текущей на указанные значения.
         *
         * @param x смещение по оси абсцисс
         * @param y смещение по оси ординат
         * @return новая позиция
         */
        Position move(double x, double y) {
            return new Position(this.x + x, this.y + y);
        }
    }
}
