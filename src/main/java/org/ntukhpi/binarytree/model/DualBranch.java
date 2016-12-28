package org.ntukhpi.binarytree.model;

import java.util.function.Supplier;

final class DualBranch<T extends Comparable<? super T>> extends NonEmptyTree<T> {

    private final ImmutableBinaryTree<T> leftChild;

    private final ImmutableBinaryTree<T> rightChild;

    DualBranch(final T value, final ImmutableBinaryTree<T> left, final ImmutableBinaryTree<T> right) {
        super(value);

        if (left.isEmpty() || right.isEmpty()) {
            throw new TreeNodeValueException("Can't initialize dual brunch: children must not be empty. "
                    + "\nRight child: " + String.valueOf(value)
                    + "\nLeft child: " + String.valueOf(value)
                    + "\nValue: " + String.valueOf(value));
        }

        leftChild = left;
        rightChild = right;
    }

    @Override
    public ImmutableBinaryTree<T> left() {
        return leftChild;
    }

    @Override
    public ImmutableBinaryTree<T> right() {
        return rightChild;
    }

    @Override
    protected ImmutableBinaryTree<T> cut() {
        //        return new DualBranch<>(left().max().get(), left().remove(left().max().get()), right());
        Supplier<TreeNodeValueException> exceptionSupplier = () ->
                new TreeNodeValueException("This node shouldn't have null value, tree structure: " + toString());
        T minMax; //max of left branch or min of right branch - it will become a new root
        ImmutableBinaryTree<T> tree;

        if (left().traverse(Traversal.IN_ORDER).size() >= right().traverse(Traversal.IN_ORDER).size()) { //compare the sizes of right and left branches
            minMax = left().max().orElseThrow(exceptionSupplier);
            tree = ((NonEmptyTree<T>) clear().insert(minMax)).replaceChildren(leftChild.remove(minMax), rightChild);
        } else {
            minMax = right().min().orElseThrow(exceptionSupplier);
            tree = ((NonEmptyTree<T>) clear().insert(minMax)).replaceChildren(leftChild, rightChild.remove(minMax));
        }

        return tree;
    }


}
