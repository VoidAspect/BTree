package com.ntukhpi.binarytree.controller;

import com.ntukhpi.binarytree.graph.BTreeGraph;
import com.ntukhpi.binarytree.graph.Style;
import com.ntukhpi.binarytree.model.Traversal;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Контроллер элементов раскладки "layout.fxml", отображаемых на основном окне.
 * <br>Инкапсулирует обработку действий пользователя, содержит и управляет визуальным представлением
 * дерева - обьектом класса {@link BTreeGraph}.
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
     * Нижняя граница (не включительно) генерации случайных чисел.
     */
    private static final int LOWER_BOUND_RANDOM = -999;

    /**
     * Разделитель " -> " для конкатенации строк при составлении сообщения об обходе в методе {@link BTreeGraph#getTraversal(Traversal)}.
     */
    private static final Collector<CharSequence, ?, String> TRAVERSAL_ORDER_ARROW = Collectors.joining(" => ");

    /**
     * Длительность задержки при выделении вершины при анимации обхода.
     */
    private static final Duration SELECTION_DURATION = Duration.millis(500);

    /**
     * Визуальное представление бинарного дерева поиска
     */
    private final BTreeGraph treeGraph = new BTreeGraph();

    /**
     * Карта номеров-значений вершин графа.
     * Ключ - номер вершины при текущем обходе
     * Значение - число, записанное в вершине с данным номером
     */
    private final NavigableMap<Integer, Integer> cellNumberMap = new TreeMap<>();

    /**
     * Обьект управления анимацией.
     * <br>Реализован таким образом, что производит поиск элементов
     * на визуальном отображении дерева в порядке обхода,
     * выбирая новый элемент каждые 0.5. секунд.
     * Когда элементы, задекларированные в {@link LayoutController#cellNumberMap} заканчиваются,
     * вызывает метод {@link LayoutController#stop()}
     */
    private final Animation animation = new Timeline(new KeyFrame(
            SELECTION_DURATION,
            actionEvent -> {
                if (!cellNumberMap.isEmpty()) {
                    treeGraph.findNode(cellNumberMap.pollFirstEntry().getValue());
                } else {
                    stop();
                }
            }));

    private Toggle traverseMode;

    /*##############################
     #                             #
     # JavaFX elements             #
     #                             #
     ##############################*/

    /* Animation controls */

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
    }

    /**
     * Выделить элемент дерева с наименьшим значением.
     */
    @FXML
    public void findMin() {
        treeGraph.min();
    }

    /**
     * Выделить элемент дерева с наибольшим значением.
     */
    @FXML
    public void findMax() {
        treeGraph.max();
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
     * иначе добавляет и ввыделяет новую.
     * <br>По кнопке DELETE - удаляет віделенную ячейку.
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
        Text text;
        traverseMode = Optional.ofNullable(orders.getSelectedToggle())
                .orElseGet(() -> {
                    orders.selectToggle(traverseMode);
                    return traverseMode;
                });

        if (togglePreOrder.equals(traverseMode)) {
            cellValues = treeGraph.getTraversalPreOrder();
            text = preOrderOut;
        } else if (togglePostOrder.equals(traverseMode)) {
            cellValues = treeGraph.getTraversalPostOrder();
            text = postOrderOut;
        } else if (toggleInOrder.equals(traverseMode)) {
            cellValues = treeGraph.getTraversalInOrder();
            text = inOrderOut;
        } else {
            throw new IllegalStateException("One traverse order should always be selected");
        }
        lightUpText(text);
        cellNumberMap.clear();
        cellNumberMap.putAll(mapArrayValsToPositions(cellValues));
        animation.jumpTo(Duration.ZERO);
    }

    /**
     * Подсветить текст.
     *
     * @param text визуальній компонент "текст".
     */
    private void lightUpText(Text text) {
        console.getChildren().forEach(node -> node.getStyleClass().
                removeIf(Predicate.isEqual(Style.CONSOLE_OUT_SELECTED.getStyleClass())));
        text.getStyleClass().add(Style.CONSOLE_OUT_SELECTED.getStyleClass());
    }

    private void disableControls(boolean disable) {
        Set<Node> controls = sideBar.lookupAll(".button")
                .stream()
                .filter(node -> !node.getStyleClass().contains(Style.ANIMATION_BUTTON.getStyleClass()))
                .collect(Collectors.toSet());
        controls.forEach(node -> node.setDisable(disable));
        orders.getToggles().forEach(toggle -> ((Node) toggle).setDisable(disable));
    }

    /**
     * Обновить содержимое нижней панели на основе значений, полученных из {@link LayoutController#treeGraph}.
     * <br>Также вызывет {@link LayoutController#initTraversalMode()}.
     */
    private void updateTraversal() {
        preOrderOut.setText(Arrays.stream(treeGraph.getTraversalPreOrder())
                .map(String::valueOf)
                .collect(TRAVERSAL_ORDER_ARROW));
        postOrderOut.setText(Arrays.stream(treeGraph.getTraversalPostOrder())
                .map(String::valueOf)
                .collect(TRAVERSAL_ORDER_ARROW));
        inOrderOut.setText(Arrays.stream(treeGraph.getTraversalInOrder())
                .map(String::valueOf)
                .collect(TRAVERSAL_ORDER_ARROW));
        initTraversalMode();
    }

    /**
     * Проверить наличие целого числа в поле ввода и вернуть его внутри контейнера Optional.
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
     * Утилитарный статический метод для создания структуры данных,
     * соответствующей спецификации {@link LayoutController#cellNumberMap}
     *
     * @param vals массив значений
     * @param <T> тип-аргумент, представляющий собой любой сравниваемый с самим собой тип.
     *
     * @return карту с ключами - индексами значений массива и значениям - соотвевтсятвующими значениями элементов массива.
     */
    private static <T extends Comparable<T>> Map<Integer, T> mapArrayValsToPositions(T[] vals) {
        List<T> asList = Arrays.asList(vals);
        return asList.stream().collect(Collectors.toMap(asList::indexOf, Function.identity()));
    }
}
