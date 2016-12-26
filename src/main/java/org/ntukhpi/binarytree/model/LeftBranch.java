package org.ntukhpi.binarytree.model;


final class LeftBranch<T extends Comparable<T>> extends SingleBranch<T> {

    LeftBranch(final T val, final ImmutableBinaryTree<T> child) {
        super(val, child);
    }

    @Override
    public ImmutableBinaryTree<T> left() {
        return getChild();
    }

    @Override
    public ImmutableBinaryTree<T> right() {
        return EmptyTree.instance();
    }

}
