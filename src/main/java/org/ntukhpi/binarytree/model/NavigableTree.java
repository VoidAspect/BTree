package org.ntukhpi.binarytree.model;

import java.util.Optional;

/**
 * Интерфейс {@code NavigableTree} расширяет интерфейс {@link Tree}
 * методами навигации по дереву и получения доп. информации о его структуре.
 * <br> Этот функционал может быть невостребованым с точки зрения практического применения бинарного дерева поиска,
 * но он полезен для реализации данного проекта для более гибкой работы с моделью данных.
 *
 * @author Alexander Gorbunov
 * @see Tree
 */
public interface NavigableTree<T extends Comparable<T>> extends Tree<T> {

    /**
     * Получение правого поддерева.
     *
     * @return правое поддерево данного узла.
     */
    NavigableTree<T> right();

    /**
     * Получение левого поддерева.
     *
     * @return левое поддерево данного узла.
     */
    NavigableTree<T> left();

    /**
     * Получение значения корня дерева.
     * Если дерево пустое, {@link Optional} должен хранить пустую ссылку (null).
     *
     * @return контейнер, который может содержать значение корня дерева.
     */
    Optional<T> getRoot();

    /**
     * Получение высоты дерева - т.е. длины
     * наибольшего из существующих в нем прямых маршрутов от корня до листа,
     * измеренного в пройденных узлах.
     * <br>Высота пустого дерева = 0.
     *
     * @return высота дерева.
     */
    int height();

}
