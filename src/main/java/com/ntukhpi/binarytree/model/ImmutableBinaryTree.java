package com.ntukhpi.binarytree.model;

/**
 * @author Alexander Gorbunov
 */
abstract class ImmutableBinaryTree<T extends Comparable<T>> implements NavigableTree<T> {

    @Override
    public abstract ImmutableBinaryTree<T> insert(T value);

    @Override
    public abstract ImmutableBinaryTree<T> remove(T value);

    @Override
    public abstract ImmutableBinaryTree<T> left();

    @Override
    public abstract ImmutableBinaryTree<T> right();

    protected abstract ImmutableBinaryTree<T> cut();

    protected abstract ImmutableBinaryTree<T> insertAll(ImmutableBinaryTree<T> tree);

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
