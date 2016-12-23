package com.ntukhpi.binarytree.controller;

import com.ntukhpi.binarytree.graph.BTreeGraph;
import com.ntukhpi.binarytree.graph.Style;
import com.ntukhpi.binarytree.model.Traversal;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Alexander Gorbunov
 */
public class LayoutController implements Initializable {

    private static final Random RANDOM = new Random();

    private static final int UPPER_BOUND_RANDOM = 1000;

    private static final int LOWER_BOUND_RANDOM = -999;

    /**
     * Разделитель " -> " для конкатенации строк при составлении сообщения об обходе в методе {@link BTreeGraph#getTraversalOrder(Traversal)}.
     */
    private static final Collector<CharSequence, ?, String> TRAVERSAL_ORDER_ARROW = Collectors.joining(" => ");


    /**
     * Visual representation of a binary onKeyPressed tree.
     */
    private final BTreeGraph treeGraph = new BTreeGraph();

    /*##############################
     #                             #
     # JavaFX elements             #
     #                             #
     ##############################*/

    @FXML
    private TextField input;

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

    @FXML
    private AnchorPane rightControlGroup;

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
    }

    /*##############################
     #                             #
     # JavaFX handlers             #
     #                             #
     ##############################*/

    @FXML
    public void insert() {
        getInput().ifPresent(treeGraph::addNode);
        updateConsole();
    }

    @FXML
    public void insertRandom() {
        RANDOM.setSeed(System.currentTimeMillis());
        int randomValue = RANDOM.nextInt(UPPER_BOUND_RANDOM * 2) + LOWER_BOUND_RANDOM;
        treeGraph.addNode(randomValue);
        updateConsole();
    }

    @FXML
    public void remove() {
        Optional<Integer> input = getInput();
        if (input.isPresent()) {
            treeGraph.removeNode(input.get());
        } else {
            treeGraph.removeNode();
        }
        updateConsole();
    }

    @FXML
    public void mutate() {
        getInput().ifPresent(treeGraph::mutateNode);
        updateConsole();
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
    public void play(ActionEvent actionEvent) {  //todo add animation controls
        String buttonId = ((Button) actionEvent.getSource()).getId();
        playAnimation(PlayAction.getById(buttonId));
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
        updateConsole();
    }

    @FXML
    public void clean() {
        treeGraph.clear();
        input.clear();
        updateConsole();
    }

    @FXML
    public void close() {
        Platform.exit();
    }

    private void playAnimation(PlayAction action) {  //todo implement animation
        Set<Node> controls = sideBar.lookupAll(".button")
                .stream()
                .filter(node -> !node.getStyleClass().contains(Style.ANIMATION_BUTTON.getStyleClass()))
                .collect(Collectors.toSet());

        if (action == PlayAction.START) {
            controls.forEach(node -> node.setDisable(true));
        }
        controls.forEach(node -> node.setDisable(false));
    }

    private void updateConsole() {
        preOrderOut.setText(Arrays.stream(treeGraph.getTraversalPreOrder())
                .map(String::valueOf)
                .collect(TRAVERSAL_ORDER_ARROW));
        postOrderOut.setText(Arrays.stream(treeGraph.getTraversalPostOrder())
                .map(String::valueOf)
                .collect(TRAVERSAL_ORDER_ARROW));
        inOrderOut.setText(Arrays.stream(treeGraph.getTraversalInOrder())
                .map(String::valueOf)
                .collect(TRAVERSAL_ORDER_ARROW));
    }

    private Optional<Integer> getInput() {
        String input = this.input.getText();

        Integer value;
        if (input.matches("^(-?)\\d+")) {
            value = Integer.parseInt(input);
        } else {
            value = null;
            this.input.clear();
        }

        return Optional.ofNullable(value);
    }

    private enum PlayAction {
        START("start"), PAUSE("pause"), STOP("stop"), NONE;

        private String id;

        PlayAction() {
        }

        PlayAction(String id) {
            this.id = id;
        }

        static PlayAction getById(String id) {
           return Arrays.stream(values())
                   .filter(v -> v.id.equals(id))
                   .findFirst()
                   .orElse(NONE);
        }
    }
}
