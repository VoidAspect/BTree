package org.ntukhpi.binarytree.model;


import java.util.*;

/**
 * Фабрика обьектов класса {@link ImmutableBinaryTree} и подклассов.
 * <br>Предоставляет методы для создания новых бинарных деревьев.
 */
public final class TreeFactory {

    /**
     * Метод инициализации пустого дерева.
     *
     * @param <U> тип-параметр значений, которые будут хранится в дереве.
     * @return новое пустое дерево.
     */
    public final <U extends Comparable<U>> ImmutableBinaryTree<U> immutableTree() {
        return EmptyTree.instance();
    }

    /**
     * Метод инициализации дерева с указанными значениями.
     * <br>Значения добавляются в дерево в том порядке, в котором они переданы в метод.
     * <br>Сбалансированность полученного дерева не гарантируется.
     *
     * @param elements массив значений.
     * @param <U>      тип-параметр значений, которые будут хранится в дереве.
     * @return новое дерево с переданными значениями в вершинах.
     */
    @SafeVarargs
    public final <U extends Comparable<U>> ImmutableBinaryTree<U> immutableTree(final U... elements) {
        ImmutableBinaryTree<U> tree = immutableTree();
        for (U elem : elements) {
            tree = tree.insert(elem);
        }
        return tree;
    }

    /**
     * Метод инициализации сбалансированного дерева с указанными значениями.
     * <br>Значения сортируются и добавляются в дерево через метод {@link TreeFactory#populateTree(List)}
     * <br>Сбалансированность полученного дерева гарантируется.
     *
     * @param elements массив значений.
     * @param <U>      тип-параметр значений, которые будут хранится в дереве.
     * @return новое сбалансированное дерево с переданными значениями в вершинах.
     * @see TreeFactory#populateTree(List)
     */
    @SafeVarargs
    public final <U extends Comparable<U>> ImmutableBinaryTree<U> balancedTree(final U... elements) {
        List<U> asList = Arrays.asList(elements);
        asList.sort(Comparator.naturalOrder());
        return populateTree(asList);
    }

    /**
     * Метод принимает отсортированный список значений,
     * создает новое пустое дерево
     * и рекурсивно добавляет в него среднее значение списка, потом среднее значение каждой из половин списка
     * - в корень правого и левого поддеревьев и.т.п., пока не иссякнут значения в списке.
     *
     * @param elements отсортированный список значений
     * @param <U>      тип-параметр значений, которые будут хранится в дереве.
     * @return новое сбалансированное дерево с переданными значениями в вершинах.
     */
    private <U extends Comparable<U>> ImmutableBinaryTree<U> populateTree(List<U> elements) {
        if (elements.isEmpty()) return immutableTree();

        ImmutableBinaryTree<U> tree = immutableTree();

        int mid = elements.size() / 2;
        tree = tree.insert(elements.get(mid));
        tree = tree.insertAll(populateTree(elements.subList(0, mid)));
        tree = tree.insertAll(populateTree(elements.subList(mid + 1, elements.size())));
        return tree;
    }

}
