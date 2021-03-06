package org.ntukhpi.binarytree.model;

/**
 * @author Alexander Gorbunov
 */
final class Leaf<T extends Comparable<? super T>> extends NonEmptyTree<T> {

    Leaf(final T val) {
        super(val);
    }

    @Override
    public ImmutableBinaryTree<T> left() {
        return EmptyTree.instance();
    }

    @Override
    public ImmutableBinaryTree<T> right() {
        return EmptyTree.instance();
    }

    @Override
    protected ImmutableBinaryTree<T> cut() {
        return EmptyTree.instance();
    }

}
