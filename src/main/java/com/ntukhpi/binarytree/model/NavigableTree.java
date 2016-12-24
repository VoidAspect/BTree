package com.ntukhpi.binarytree.model;

import java.util.Optional;

/**
 * @author Alexander Gorbunov
 */
public interface NavigableTree<T extends Comparable<T>> extends Tree<T> {

    NavigableTree<T> right();

    NavigableTree<T> left();

    Optional<T> getRoot();

    int height();

}
