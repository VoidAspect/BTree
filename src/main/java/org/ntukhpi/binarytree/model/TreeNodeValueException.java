package org.ntukhpi.binarytree.model;

/**
 * Исключение, выбрасываемое при нарущении консистентности структуры данных дерева.
 * <br>Например, может быть выброшено при попытке создать в коде узел дерева со значением null.
 * <br>Реализация в рамках данного проекта должна полностью исключить возможность
 * возникновения этого исключения за пределами пакета {@code model}.
 * @author Alexander Gorbunov
 */
@SuppressWarnings("WeakerAccess")
public class TreeNodeValueException extends RuntimeException {

    public TreeNodeValueException(String message) {
        super(message);
    }
}
