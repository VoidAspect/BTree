package com.ntukhpi.binarytree;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Alexander Gorbunov
 */
public class BTree extends Application {

    private static final int MIN_HEIGHT = 520;

    private static final int MIN_WIDTH = 300;

    private static final Image ICON = new Image(BTree.class.getResourceAsStream("/icon.png"));

    private static final String TITLE = "Binary Tree Demo";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/layout.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle(TITLE);
        primaryStage.getIcons().add(ICON);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
