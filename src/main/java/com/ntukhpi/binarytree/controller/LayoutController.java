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
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
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

    private final Animation animation = new Timeline(new KeyFrame(
            SELECTION_DURATION,
            actionEvent -> {
                if (!cellNumberMap.isEmpty()) {
                    treeGraph.findNode(cellNumberMap.pollFirstEntry().getValue());
                } else {
                    stop();
                }
            }));

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
    private Text preOrderOut;
    @FXML
    private Text postOrderOut;
    @FXML
    private Text inOrderOut;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewArea.prefWidthProperty().bind(workSpace.widthProperty().subtract(rightControlGroup.getWidth()));
        Group content = treeGraph.getContent();
        board.getChildren().add(content);

//        orders.getToggles().forEach(toggle -> toggle.g);

        //initialize animation
        animation.setCycleCount(Animation.INDEFINITE);
    }

    /*##############################
     #                             #
     # JavaFX handlers             #
     #                             #
     ##############################*/


    @FXML
    public void play() {
        if (!treeGraph.isEmpty()) {
            animation.play();
            disableControls(true);
        }
    }

    @FXML
    public void pause() {
        animation.pause();
        disableControls(false);
    }

    @FXML
    public void stop() {
        animation.stop();
        treeGraph.unselect();
        disableControls(false);
        initCellNumberMap();
    }

    @FXML
    public void insert() {
        getInput().ifPresent(treeGraph::addNode);
        updateTraversal();
    }

    @FXML
    public void insertRandom() {
        RANDOM.setSeed(System.currentTimeMillis());
        int randomValue = RANDOM.nextInt(UPPER_BOUND_RANDOM * 2) + LOWER_BOUND_RANDOM;
        treeGraph.addNode(randomValue);
        updateTraversal();
    }

    @FXML
    public void remove() {
        Optional<Integer> input = getInput();
        if (input.isPresent()) {
            treeGraph.removeNode(input.get());
        } else {
            treeGraph.removeNode();
        }
        updateTraversal();
    }

    @FXML
    public void mutate() {
        getInput().ifPresent(treeGraph::mutateNode);
        updateTraversal();
    }

    @FXML
    public void findMin() {
        treeGraph.min();
    }

    @FXML
    public void findMax() {
        treeGraph.max();
    }

    @FXML
    public void hideSideBar() {
        if (!sideBarToggle.isSelected()) {
            rightControlGroup.getChildren().remove(sideBar);
        } else {
            rightControlGroup.getChildren().add(sideBar);
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode.equals(KeyCode.ENTER)) {
            getInput().ifPresent(treeGraph::findNode);
        } else if (keyCode.equals(KeyCode.DELETE)) {
            remove();
        }
    }

    @FXML
    public void balance() {
        treeGraph.balance();
        updateTraversal();
    }

    @FXML
    public void clean() {
        treeGraph.clear();
        input.clear();
        updateTraversal();
    }

    @FXML
    public void close() {
        Platform.exit();
    }

    @FXML
    public void initCellNumberMap() {
        cellNumberMap.clear();

        Integer[] cellValues1;
        Toggle selectedToggle = orders.getSelectedToggle();
        if (selectedToggle.equals(togglePreOrder)) {
            cellValues1 = treeGraph.getTraversalPreOrder();
        } else if (selectedToggle.equals(togglePostOrder)) {
            cellValues1 = treeGraph.getTraversalPostOrder();
        } else if (selectedToggle.equals(toggleInOrder)) {
            cellValues1 = treeGraph.getTraversalInOrder();
        } else {
            throw new IllegalStateException("One traverse order should always be selected");
        }
        Integer[] cellValues = cellValues1;

        cellNumberMap.putAll(mapArrayValsToPositions(cellValues));
    }

    private void disableControls(boolean disable) {
        Set<Node> controls = sideBar.lookupAll(".button")
                .stream()
                .filter(node -> !node.getStyleClass().contains(Style.ANIMATION_BUTTON.getStyleClass()))
                .collect(Collectors.toSet());
        controls.forEach(node -> node.setDisable(disable));
    }

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
        initCellNumberMap();
    }

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


    private static <T extends Comparable<T>> Map<Integer, T> mapArrayValsToPositions(T[] vals) {
        Map<Integer, T> posMap = new TreeMap<>();
        for (int i = 0; i < vals.length; i++) {
            posMap.put(i, vals[i]);
        }
        return posMap;
//        return Arrays.stream(vals)
//                .collect(Collectors.toMap((T e) -> Arrays.binarySearch(vals, e), Function.identity()));
    }
}
