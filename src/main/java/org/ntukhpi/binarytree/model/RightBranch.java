package org.ntukhpi.binarytree.model;

final class RightBranch<T extends Comparable<T>> extends SingleBranch<T> {

    RightBranch(final T val, final ImmutableBinaryTree<T> child) {
        super(val, child);
    }

    @Override
    public ImmutableBinaryTree<T> left() {
        return EmptyTree.instance();
    }

    @Override
    public ImmutableBinaryTree<T> right() {
        return getChild();
    }

}
