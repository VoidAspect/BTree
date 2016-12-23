package com.ntukhpi.binarytree.controller;

import com.ntukhpi.binarytree.graph.BTreeGraph;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

/**
 * @author Alexander Gorbunov
 */
public class LayoutController implements Initializable {

    private static final Random RANDOM = new Random();

    private static final int UPPER_BOUND_RANDOM = 1000;

    private static final int LOWER_BOUND_RANDOM = -999;

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
        board.getChildren().add(treeGraph.getContent());
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

    private void updateConsole() {
        preOrderOut.setText(treeGraph.getTraversalPreOrder());
        postOrderOut.setText(treeGraph.getTraversalPostOrder());
        inOrderOut.setText(treeGraph.getTraversalInOrder());
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

}
