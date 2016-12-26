package org.ntukhpi.binarytree.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexander Gorbunov
 */
final class EmptyTree <T extends Comparable<T>> extends ImmutableBinaryTree<T> {

    private static final EmptyTree INSTANCE = new EmptyTree();

    private EmptyTree() {
    }

    @SuppressWarnings("unchecked")
    static <U extends Comparable<U>> ImmutableBinaryTree<U> instance() {
        return INSTANCE;
    }

    @Override
    public ImmutableBinaryTree<T> insert(final T element) {
        return new Leaf<>(element);
    }

    @Override
    public ImmutableBinaryTree<T> remove(final T element) {
        return this;
    }

    @Override
    public ImmutableBinaryTree<T> right() {
        return this;
    }

    @Override
    protected ImmutableBinaryTree<T> cut() {
        return this;
    }

    @Override
    public ImmutableBinaryTree<T> left() {
        return this;
    }

    @Override
    protected ImmutableBinaryTree<T> insertAll(ImmutableBinaryTree<T> tree) {
        return tree;
    }

    @Override
    public boolean contains(final T value) {
        return false;
    }

    @Override
    public String toString() {
        return "_";
    }

    @Override
    public Optional<T> min() {
        return Optional.empty();
    }

    @Override
    public Optional<T> max() {
        return Optional.empty();
    }

    @Override
    public List<T> traverse(Traversal order) {
        return Collections.emptyList();
    }

    @Override
    public Optional<T> getRoot() {
        return Optional.empty();
    }
}
