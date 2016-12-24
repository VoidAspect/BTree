package com.ntukhpi.binarytree.model;

abstract class SingleBranch<T extends Comparable<T>> extends NonEmptyTree<T> {
    private final ImmutableBinaryTree<T> child;

    SingleBranch(final T val, final ImmutableBinaryTree<T> childTree) {
        super(val);
        child = childTree;
    }

    final ImmutableBinaryTree<T> getChild() {
        return child;
    }

    @Override
    protected ImmutableBinaryTree<T> cut() {
        return child;
    }

}
