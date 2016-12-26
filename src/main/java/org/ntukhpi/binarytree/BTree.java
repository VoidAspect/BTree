package org.ntukhpi.binarytree;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Приложение {@code BTree} является визуальной демонстрацией бинарного дерева поиска.
 * <br>Разработано в рамках курса "Основы Дисретной Математики" факультета КИТ НТУ "ХПИ".
 * <br>Визуальная модель позволяет добавлять, удалять, изменять значения в бинарном дереве.
 *
 * @author Alexander Gorbunov
 */
public class BTree extends Application {

    /**
     * Минимальная высота окна в пикселях.
     */
    private static final int MIN_HEIGHT = 520;

    /**
     * Минимальная ширина окна в пикселях.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Значок приложения.
     */
    private static final Image ICON = new Image(BTree.class.getResourceAsStream("/icon.png"));

    /**
     * Заголовок окна.
     */
    private static final String TITLE = "Binary Search Tree";

    /**
     * Метод запуска приложения по основному дейсствию.
     * Инициализирует компоненты окна, его параметры, загаловок, значок и.т.п.
     *
     * @param primaryStage абстракция над главным (и единственным) окном приложения.
     * @throws Exception любое исключение в зоде выполнения работы приложения.
     */
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

    /**
     * Точка входа в программу.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
