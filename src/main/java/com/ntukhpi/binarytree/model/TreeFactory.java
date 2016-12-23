package com.ntukhpi.binarytree.model;


import java.util.*;

public final class TreeFactory {

    public final <U extends Comparable<U>> ImmutableBinaryTree<U> immutableTree() {
        return EmptyTree.instance();
    }

    @SafeVarargs
    public final <U extends Comparable<U>> ImmutableBinaryTree<U> immutableTree(final U... elems) {
        ImmutableBinaryTree<U> tree = immutableTree();
        for (U elem : elems) {
            tree = tree.insert(elem);
        }
        return tree;
    }

    @SafeVarargs
    public final <U extends Comparable<U>> ImmutableBinaryTree<U> balancedTree(final U... elems) {
        return populateTree(Arrays.asList(elems));
    }

    private <U extends Comparable<U>>ImmutableBinaryTree<U> populateTree(List<U> elems) {
        elems.sort(Comparator.naturalOrder());

        if (elems.isEmpty()) return immutableTree();

        ImmutableBinaryTree<U> tree = immutableTree();

        int mid = elems.size() / 2;
        tree = tree.insert(elems.get(mid));
        tree = tree.insertAll(populateTree(elems.subList(0, mid)));
        tree = tree.insertAll(populateTree(elems.subList(mid + 1, elems.size())));
        return tree;
    }

}
