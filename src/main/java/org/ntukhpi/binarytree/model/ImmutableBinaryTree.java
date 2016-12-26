package org.ntukhpi.binarytree.model;

/**
 * Класс {@code ImmutableBinaryTree} является
 * вершиной иерархии классов реализации интерфейса {@link NavigableTree}, которая используется в данном проекте.
 * <br>Согласно задумке, данная реализация является иммутабельной (неизменяемой) и персистентной
 * (т.е. структура данных, которая при внесении в нее каких-то изменений сохраняет все свои предыдущие состояния и доступ к этим состояниям)
 * <br>Такие свойства обеспечат данной реализации потокобезопасность, ссылочную прозрачность и разумное использование памяти.
 * <br>В перспективе, это позволит кэшировать и версионировать данную структуру данных, делая ее еще более производительной.
 * <p>
 * <p>Данный абстрактный класс имеет двух потомков:
 * <br> - {@link EmptyTree} - пустое дерево. Final класс, синглтон.
 * <br> - {@link NonEmptyTree} - непустое дерево. Абстрактный класс, отвесающий за подиерархию непустых деревьев - "листьев" и "веток".
 *
 * @author Alexander Gorbunov
 */
abstract class ImmutableBinaryTree<T extends Comparable<T>> implements NavigableTree<T> {

    /**
     * Метод удаления корня дерева.
     * <br>Реализация должна гарантировать сбалансированную замену корня:
     * <br> - если правое поддерево содержит больще элементов, чем левое -
     * то новым корнем станет минимальный элемент правого поддерева.
     * <br> - если левое поддерево содержит больще элементов, чем правое -
     * то новым корнем станет максимальный элемент левого поддерева.
     *
     * @return новое дерево, образованное удалением корня из текущего.
     */
    protected abstract ImmutableBinaryTree<T> cut();

    /**
     * Метод вставки поддерева в текущее дерево.
     *
     * @param tree поддерево, которое необходимо слить с текущим.
     * @return новое дерево, содержащее все уникальные элементы из исходного и вставленного деревьев.
     */
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

    /**
     * Рекурсивный метод получения высоты дерева.
     *
     * @param level текущий уровень вершины на дереве, считая от корня.
     *              Инициализируется с изначальным значением = 1.
     * @return высота дерева
     */
    private int getHeight(int level) {

        ImmutableBinaryTree<T> left = left();
        ImmutableBinaryTree<T> right = right();

        int nextLevel = level + 1;

        if (left.isEmpty() && right.isEmpty()) {
            return level;
        }

        int rightHeight = right.getHeight(nextLevel);
        int leftHeight = left.getHeight(nextLevel);
        if (left.isEmpty()) {
            return rightHeight;
        } else if (right.isEmpty()) {
            return leftHeight;
        } else if (leftHeight > rightHeight) {
            return leftHeight;
        } else {
            return rightHeight;
        }


    }

}
