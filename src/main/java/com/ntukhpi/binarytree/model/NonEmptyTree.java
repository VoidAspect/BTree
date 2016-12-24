package com.ntukhpi.binarytree.model;

import java.util.*;

/**
 * @author Alexander Gorbunov
 */
abstract class NonEmptyTree<T extends Comparable<T>> extends ImmutableBinaryTree<T> {

    private final T value;

    NonEmptyTree(final T val) {
        value = Optional.ofNullable(val).orElseThrow(() ->
                new TreeNodeValueException("This node shouldn't have null value! Value passed for check: "
                        + val + "; tree structure: "
                        + toString()));
    }

    @Override
    public final ImmutableBinaryTree<T> insert(final T element) {
        if (!Optional.ofNullable(element).isPresent()) return this;

        ImmutableBinaryTree<T> tree;
        if (value.equals(element)) {
            tree = this;
        } else {
            tree = insertAll(new Leaf<>(element));
        }
        return tree;
    }

    @Override
    public final ImmutableBinaryTree<T> remove(final T element) {
        if (!Optional.ofNullable(element).isPresent()) return this;

        ImmutableBinaryTree<T> tree;
        if (value.equals(element)) {
            tree = cut();
        } else if (value.compareTo(element) > 0) {
            tree = replaceChildren(left().remove(element), right());
        } else {
            tree = replaceChildren(left(), right().remove(element));
        }
        return tree;
    }

    @Override
    protected ImmutableBinaryTree<T> insertAll(final ImmutableBinaryTree<T> tree) {
        ImmutableBinaryTree<T> newTree;
        if (tree == EmptyTree.instance()) {
            newTree = this;
        } else {
            NonEmptyTree<T> nonEmptyTree = (NonEmptyTree<T>) tree;
            if (value.equals(nonEmptyTree.value)) {
                newTree = replaceChildren(left().insertAll(nonEmptyTree.left()),
                        right().insertAll(nonEmptyTree.right()));
            } else if (value.compareTo(nonEmptyTree.value) > 0) {
                newTree = replaceChildren(left().insertAll(tree), right());
            } else {
                newTree = replaceChildren(left(), right().insertAll(tree));
            }
        }
        return newTree;
    }

    ImmutableBinaryTree<T> replaceChildren(final ImmutableBinaryTree<T> leftBranch, final ImmutableBinaryTree<T> rightBranch) {
        ImmutableBinaryTree<T> tree;
        if (leftBranch == left() && rightBranch == right()) {
            tree = this;
        } else if (leftBranch == EmptyTree.instance() && rightBranch == EmptyTree.instance()) {
            tree = new Leaf<>(value);
        } else if (leftBranch == EmptyTree.instance()) {
            tree = new RightBranch<>(value, rightBranch);
        } else if (rightBranch == EmptyTree.instance()) {
            tree = new LeftBranch<>(value, leftBranch);
        } else {
            tree = new DualBranch<>(value, leftBranch, rightBranch);
        }
        return tree;
    }

    @Override
    public boolean contains(T element) {
        if (!Optional.ofNullable(element).isPresent()) return false;

        boolean contains;
        if (this.value.equals(element)) {
            contains = true;
        } else if (this.value.compareTo(element) > 0) {
            contains = left().contains(element);
        } else {
            contains = right().contains(element);
        }
        return contains;
    }

    @Override
    public Optional<T> min() {
        if (left().isEmpty()) return Optional.of(value);
        return left().min();
    }

    @Override
    public Optional<T> max() {
        if (right().isEmpty()) return Optional.of(value);
        return right().max();
    }

    @Override
    public final List<T> traverse(Traversal order) {
        List<T> nodeList;
        switch (order) {
            case PRE_ORDER:
                nodeList = traversePreOrder();
                break;
            case POST_ORDER:
                nodeList = traversePostOrder();
                break;
            case IN_ORDER:
                nodeList = traverseInOrder();
                break;
            default:
                throw new UnsupportedOperationException("Such order of traversal is not supported: " + order);
        }
        return nodeList;
    }
    private List<T> traversePreOrder() {
        List<T> list = new LinkedList<>();
        list.add(value);
        list.addAll(left().traverse(Traversal.PRE_ORDER));
        list.addAll(right().traverse(Traversal.PRE_ORDER));
        return list;
    }

    private List<T> traversePostOrder() {
        List<T> list = new LinkedList<>();
        list.add(value);
        list.addAll(right().traverse(Traversal.POST_ORDER));
        list.addAll(left().traverse(Traversal.POST_ORDER));
        return list;
    }

    private List<T> traverseInOrder() {
        List<T> list = new LinkedList<>();
        list.addAll(left().traverse(Traversal.IN_ORDER));
        list.add(value);
        list.addAll(right().traverse(Traversal.IN_ORDER));
        return list;
    }

    @Override
    public Optional<T> getRoot() {
        return Optional.of(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NonEmptyTree)) return false;
        NonEmptyTree<?> that = (NonEmptyTree<?>) o;

        return Objects.equals(value, that.value) &&
                Objects.equals(right(), that.right()) &&
                Objects.equals(left(), that.left());
    }

    @Override
    public String toString() {
        return "(" + left().toString() + " " + value + " " + right().toString() + ")";
    }

}
