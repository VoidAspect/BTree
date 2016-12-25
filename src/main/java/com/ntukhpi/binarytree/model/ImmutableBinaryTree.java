package com.ntukhpi.binarytree.model;

/**
 * Класс {@code ImmutableBinaryTree} является
 * вершиной иерархии классов реализации интерфейса {@link NavigableTree}, которая используется в данном проекте.
 * <br>Согласно задумке, данная реализация является иммутабельной (неизменяемой) и персистентной
 * (т.е. структура данных, которая при внесении в нее каких-то изменений сохраняет все свои предыдущие состояния и доступ к этим состояниям)
 * <br>Такие свойства обеспечат данной реализации потокобезопасность, ссылочную прозрачность и разумное использование памяти.
 * <br>В перспективе, это позволит кэшировать и версионировать данную структуру данных, делая ее еще более производительной.
 *
 * <p>Данный абстрактный класс имеет двух потомков:
 * <br> - {@link EmptyTree} - пустое дерево. Final класс, синглтон.
 * <br> - {@link NonEmptyTree} - непустое дерево. Абстрактный класс, отвесающий за подиерархию непустых деревьев - "листьев" и "веток".
 *
 * @author Alexander Gorbunov
 */
abstract class ImmutableBinaryTree<T extends Comparable<T>> implements NavigableTree<T> {

    protected abstract ImmutableBinaryTree<T> cut();

    protected abstract ImmutableBinaryTree<T> insertAll(ImmutableBinaryTree<T> tree);

    @Override
    public abstract ImmutableBinaryTree<T> insert(T value);

    @Override
    public abstract ImmutableBinaryTree<T> remove(T value);

    @Override
    public abstract ImmutableBinaryTree<T> left();

    @Override
    public abstract ImmutableBinaryTree<T> right();

    @Override
    public Tree<T> clear() {
        return EmptyTree.instance();
    }

    @Override
    public boolean isEmpty() {
        return this == EmptyTree.instance();
    }

    @Override
    public int height() {
        int level;
        if (isEmpty()) {
            level = 0;
        } else {
            level = getHeight(1);
        }
        return level;
    }

    private int getHeight(int level) {

        if (right().isEmpty() && left().isEmpty()) return level;

        ImmutableBinaryTree<T> left = left();
        ImmutableBinaryTree<T> right = right();

        int nextLevel = level + 1;

        if (left.traverse(Traversal.IN_ORDER).size() > right.traverse(Traversal.IN_ORDER).size()) {
            return left.getHeight(nextLevel);
        } else {
            return right.getHeight(nextLevel);
        }

    }

}
