package org.ntukhpi.binarytree.controller;

import javafx.collections.ObservableList;
import org.ntukhpi.binarytree.graph.BTreeGraph;
import org.ntukhpi.binarytree.graph.Style;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Контроллер элементов раскладки "layout.fxml", отображаемых на основном окне.
 * <br>Инкапсулирует обработку действий пользователя, содержит и управляет визуальным представлением
 * дерева - обьектом класса {@link BTreeGraph}.
 *
 * @author Alexander Gorbunov
 */
public class LayoutController implements Initializable {

    /**
     * Генератор случайных чисел.
     */
    private static final Random RANDOM = new Random();

    /**
     * Верхняя граница (не включительно) генерации случайных чисел.
     */
    private static final int UPPER_BOUND_RANDOM = 1000;

    /**
     * Нижняя граница (включительно) генерации случайных чисел.
     */
    private static final int LOWER_BOUND_RANDOM = -999;

    /**
     * Длительность задержки при выделении вершины при анимации обхода.
     */
    private static final Duration SELECTION_DURATION = Duration.millis(500);

    private static final String ARROW_JOINER = " -> ";

    private static final String PRE_ORDER_ID = "preOrder";

    private static final String POST_ORDER_ID = "postOrder";

    private static final String IN_ORDER_ID = "inOrder";

    /**
     * Визуальное представление бинарного дерева поиска
     */
    private final BTreeGraph treeGraph = new BTreeGraph();

    /**
     * Карта номеров-значений вершин графа.
     * <br>Ключ - номер вершины при текущем обходе.
     * <br>Значение - число, записанное в вершине с данным номером
     */
    private final NavigableMap<Integer, Integer> cellNumberMap = new TreeMap<>();

    /**
     * Обьект управления анимацией.
     * <br>Реализован таким образом, что производит поиск элементов
     * на визуальном отображении дерева в порядке обхода,
     * выбирая новый элемент каждые 0.5 секунд.
     * Когда элементы, задекларированные в {@link LayoutController#cellNumberMap} заканчиваются,
     * вызывает метод {@link LayoutController#stop()}
     */
    private final Animation animation = new Timeline(new KeyFrame(
            SELECTION_DURATION,
            actionEvent -> {
                if (!cellNumberMap.isEmpty()) {
                    treeGraph.findNode(cellNumberMap.pollFirstEntry().getValue());
                    navigateToSelected();
                } else {
                    stop();
                }
            }));

    /**
     * Простой кэш для переиспользования текстовых компонентов.
     */
    private final Map<String, Text> textCache = new HashMap<>();


    /*##############################
     #                             #
     # JavaFX elements             #
     #                             #
     ##############################*/


    /**
     * Текущая позиция переключателя режима обхода.
     */
    private Toggle traverseMode;

    /* Animation controls */

    @FXML
    private TitledPane animationPane;
    @FXML
    private ToggleGroup orders;
    @FXML
    private ToggleButton togglePreOrder;
    @FXML
    private ToggleButton togglePostOrder;
    @FXML
    private ToggleButton toggleInOrder;

    /* Console controls */

    @FXML
    private TextFlow console;
    @FXML
    private Text preOrderOut;
    @FXML
    private Text postOrderOut;
    @FXML
    private Text inOrderOut;

    /* Workspace */

    @FXML
    private FlowPane board;
    @FXML
    private ScrollPane viewArea;

    /* Sidebar controls */

    @FXML
    private AnchorPane rightControlGroup;
    @FXML
    private TextField input;
    @FXML
    private BorderPane workSpace;
    @FXML
    private VBox sideBar;
    @FXML
    private ToggleButton sideBarToggle;

    /**
     * Метод инициализации контроллера, вызываемый после инициалтизации визуальных компонентов.
     * Служит для вторичной программной настройки компонентов.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewArea.prefWidthProperty().bind(workSpace.widthProperty().subtract(rightControlGroup.getWidth()));
        Group content = treeGraph.getContent();
        board.getChildren().add(content);

        traverseMode = orders.getSelectedToggle();
        //initialize animation
        initTraversalMode();
        animation.setCycleCount(Animation.INDEFINITE);
    }


    /*##############################
     #                             #
     # JavaFX handlers             #
     #                             #
     ##############################*/


    /**
     * Запустить или продолжить анимацию обхода.
     */
    @FXML
    public void play() {
        if (!treeGraph.isEmpty()) {
            animation.play();
            disableControls(true);
        }
    }

    /**
     * Приостановить анимацию обхода.
     */
    @FXML
    public void pause() {
        animation.pause();
        disableControls(false);
    }

    /**
     * Остановить и сбросить анимацию обхода.
     */
    @FXML
    public void stop() {
        animation.stop();
        treeGraph.unselect();
        disableControls(false);
        initTraversalMode();
    }

    /**
     * Добавить значение из поля ввода (если оно является целым числом) как новый элемент дерева.
     */
    @FXML
    public void insert() {
        getInput().ifPresent(treeGraph::addNode);
        updateTraversal();
        navigateToSelected();
    }

    /**
     * Добавить случайное значение как новый элемент дерева.
     */
    @FXML
    public void insertRandom() {
        RANDOM.setSeed(System.currentTimeMillis());
        int randomValue = RANDOM.nextInt(UPPER_BOUND_RANDOM * 2) + LOWER_BOUND_RANDOM;
        treeGraph.addNode(randomValue);
        updateTraversal();
        navigateToSelected();
    }

    /**
     * Удалить выделенный элемент дерева.
     */
    @FXML
    public void remove() {
        treeGraph.removeNode();
        updateTraversal();
    }

    /**
     * Изменить значение выделенного элемента дерева на значение из поля ввода.
     */
    @FXML
    public void mutate() {
        getInput().ifPresent(treeGraph::mutateNode);
        updateTraversal();
        navigateToSelected();
    }

    /**
     * Выделить элемент дерева с наименьшим значением.
     */
    @FXML
    public void findMin() {
        treeGraph.min();
        navigateToSelected();
    }

    /**
     * Выделить элемент дерева с наибольшим значением.
     */
    @FXML
    public void findMax() {
        treeGraph.max();
        navigateToSelected();
    }

    /**
     * Убрать либо вернуть боковую панель управления.
     */
    @FXML
    public void hideSideBar() {
        if (!sideBarToggle.isSelected()) {
            rightControlGroup.getChildren().remove(sideBar);
        } else {
            rightControlGroup.getChildren().add(sideBar);
        }
    }

    /**
     * Общий обработчик событий клавиатуры.
     * <br>По кнопке ENTER - принимает значение из поля ввода.
     * <br>Если такое значение есть на дереве - выделяет ячейку,
     * иначе добавляет и выделяет новую.
     * <br>По кнопке DELETE - удаляет выделенную ячейку.
     *
     * @param keyEvent событие нажатия на кнопку клавиатуры.
     */
    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode.equals(KeyCode.ENTER)) {
            getInput().ifPresent(val -> {
                if (!treeGraph.findNode(val)) {
                    treeGraph.addNode(val);
                }
            });
        } else if (keyCode.equals(KeyCode.DELETE)) {
            remove();
        }
        navigateToSelected();
    }

    /**
     * Перестроить дерево в сбалансированный вид.
     */
    @FXML
    public void balance() {
        treeGraph.balance();
        updateTraversal();
    }

    /**
     * Очистить дерево.
     */
    @FXML
    public void clean() {
        treeGraph.clear();
        input.clear();
        textCache.clear();
        updateTraversal();
    }

    /**
     * Завершить работу программы.
     */
    @FXML
    public void close() {
        Platform.exit();
    }

    /**
     * Инициализировать один из порядков обхода.
     * <br>Подсвечивает соответствующую строку в нижней панели.
     * <br>Заполняет {@link LayoutController#cellNumberMap} значениями из дерева в порядке обхода.
     */
    @FXML
    public void initTraversalMode() {
        Integer[] cellValues;
        String id;
        traverseMode = Optional.ofNullable(orders.getSelectedToggle())
                .orElseGet(() -> {
                    orders.selectToggle(traverseMode);
                    return traverseMode;
                });

        if (togglePreOrder.equals(traverseMode)) {
            cellValues = treeGraph.getTraversalPreOrder();
            id = PRE_ORDER_ID;
        } else if (togglePostOrder.equals(traverseMode)) {
            cellValues = treeGraph.getTraversalPostOrder();
            id = POST_ORDER_ID;
        } else if (toggleInOrder.equals(traverseMode)) {
            cellValues = treeGraph.getTraversalInOrder();
            id = IN_ORDER_ID;
        } else {
            throw new IllegalStateException("One traverse order should always be selected");
        }
        lightUpText(id);
        cellNumberMap.clear();
        cellNumberMap.putAll(mapArrayValuesToPositions(cellValues));
        animation.jumpTo(Duration.ZERO);
    }

    /**
     * Подсветить текст.
     */
    private void lightUpText(String id) {
        console.getChildren().forEach(node -> node.getStyleClass().
                removeIf(Predicate.isEqual(Style.CONSOLE_OUT_SELECTED.getStyleClass())));
        console.lookupAll("#" + id)
                .forEach(node -> node.getStyleClass().add(Style.CONSOLE_OUT_SELECTED.getStyleClass()));
    }

    /**
     * Метод для отключения и включения кнопок боковой панели.
     * Используется при запуске и остановке анимации.
     *
     * @param disable если true, то отключить кнопки боковой панели управления.
     *                Если false - то включить их.
     */
    private void disableControls(boolean disable) {
        Set<Node> controls = sideBar.getChildren().stream()
                .filter(node -> !node.getStyleClass().contains(Style.ANIMATION_BUTTON.getStyleClass()))
                .filter(node -> !node.equals(animationPane))
                .collect(Collectors.toSet());
        controls.forEach(node -> node.setDisable(disable));
    }

    /**
     * Обновить содержимое нижней панели на основе значений, полученных из {@link LayoutController#treeGraph}.
     * <br>Также вызывет {@link LayoutController#initTraversalMode()}.
     */
    private void updateTraversal() {
        ObservableList<Node> text = console.getChildren();

        text.clear();

        text.add(preOrderOut);
        addTextElements(text, PRE_ORDER_ID, treeGraph.getTraversalPreOrder());

        text.add(postOrderOut);
        addTextElements(text, POST_ORDER_ID, treeGraph.getTraversalPostOrder());

        text.add(inOrderOut);
        addTextElements(text, IN_ORDER_ID, treeGraph.getTraversalInOrder());

        initTraversalMode();
    }

    private void addTextElements(List<Node> text, String id, Integer... elements) {

        for (int i = 0; i < elements.length; i++) {

            int element = elements[i];
            int oneBasedOrdinal = i + 1;

            String ordinalKey = id + "|ordinal|" + i;
            Text ordinal = textCache.computeIfAbsent(ordinalKey, key -> {
                Text newOrdinal;
                newOrdinal = new Text("{" + (oneBasedOrdinal) + ": ");
                newOrdinal.setId(id);
                newOrdinal.getStyleClass().add(Style.CONSOLE_OUT.getStyleClass());
                return newOrdinal;
            });
            text.add(ordinal);

            String valueKey = id + "|value|" + element;
            Text value = textCache.computeIfAbsent(valueKey, key -> {
                Text newValue = new Text(String.valueOf(element));
                newValue.getStyleClass().add(Style.CONSOLE_OUT_VALUE.getStyleClass());
                return newValue;
            });
            text.add(value);

            String enclosingKey = id + "|enclosing|" + i;
            Text enclosing = textCache.computeIfAbsent(enclosingKey, key -> {
                Text newEnclosing = new Text("}");
                newEnclosing.setId(id);
                newEnclosing.getStyleClass().setAll(ordinal.getStyleClass());
                return newEnclosing;
            });
            text.add(enclosing);

            if (elements.length - i > 1) {
                String arrowKey = id + "|->|" + i;
                Text arrow = textCache.computeIfAbsent(arrowKey, key -> {
                    Text newArrow = new Text(ARROW_JOINER);
                    newArrow.setId(id);
                    newArrow.getStyleClass().add(Style.CONSOLE_OUT_ARROW.getStyleClass());
                    return newArrow;
                });
                text.add(arrow);
            }
        }
    }

    /**
     * Проверить наличие целого числа в поле ввода и вернуть его внутри контейнера Optional.
     *
     * @return Optional, возможно содержащий целое число.
     */
    private Optional<Integer> getInput() {
        String input = this.input.getText();

        Optional<Integer> optional;
        if (input.matches("^(-?)\\d+")) {
            optional = Optional.of(Integer.parseInt(input));
        } else {
            optional = Optional.empty();
            this.input.clear();
        }

        return optional;
    }

    /**
     * Сдвинуть экран до выделенной ячейки.
     */
    private void navigateToSelected() {
        treeGraph.getSelected().ifPresent(label -> {
            Group content = treeGraph.getContent();

            double layoutX = label.getLayoutX() + BTreeGraph.CELL_RADIUS;
            double layoutMaxX = content.getBoundsInLocal().getMaxX();
            double layoutMinX = content.getBoundsInLocal().getMinX();
            double newH = (layoutX + Math.abs(layoutMinX)) / (Math.abs(layoutMaxX) + Math.abs(layoutMinX));
            viewArea.setHvalue(newH);

            double layoutY = label.getLayoutY() + BTreeGraph.CELL_RADIUS;
            double layoutMaxY = content.getBoundsInLocal().getMaxY();
            double layoutMinY = content.getBoundsInLocal().getMinY();
            double newV = (layoutY + Math.abs(layoutMinY)) / (Math.abs(layoutMaxY) + Math.abs(layoutMinY));
            viewArea.setVvalue(newV);
        });
    }


    /**
     * Утилитарный статический метод для создания структуры данных,
     * соответствующей спецификации {@link LayoutController#cellNumberMap}
     *
     * @param values массив значений
     * @param <T>    тип-аргумент, представляющий собой любой сравниваемый с самим собой тип.
     * @return карту с ключами - индексами значений массива и значениями - соответствующими значениями элементов массива.
     */
    private static <T extends Comparable<T>> Map<Integer, T> mapArrayValuesToPositions(T[] values) {
        List<T> asList = Arrays.asList(values);
        return asList.stream().collect(Collectors.toMap(asList::indexOf, Function.identity()));
    }
}
