package com.ntukhpi.binarytree.model;

import java.util.List;
import java.util.Optional;

/**
 * Interface {@code Tree}
 *
 * @author Alexander Gorbunov
 */
public interface Tree<T extends Comparable<T>> {

    Tree<T> insert(final T value);

    Tree<T> remove(final T value);

    Tree<T> clear();

    boolean contains(final T value);

    Optional<T> min();

    Optional<T> max();

    List<T> traverse(Traversal order);

    boolean isEmpty();

}
