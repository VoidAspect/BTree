package com.ntukhpi.binarytree.model;

/**
 * @author Alexander Gorbunov
 */
@SuppressWarnings("WeakerAccess")
public class TreeNodeValueException extends RuntimeException {

    public TreeNodeValueException(String message) {
        super(message);
    }
}
