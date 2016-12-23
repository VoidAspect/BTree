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

    @Override
    public Tree<T> clear() {
        return EmptyTree.instance();
    }

    @Override
    public boolean isEmpty() {
        return this == EmptyTree.instance();
    }

    protected abstract ImmutableBinaryTree<T> insertAll(ImmutableBinaryTree<T> tree);

}
