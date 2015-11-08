package SearchEngine;

/**
 * A class representing a single search node.
 * Could have two child node.
 * <br>
 * There are 3 types of the node.
 * <br>
 * AND, OR, and Keyword
 */
public class Node {
    public static final int AND_NODE = 1;
    public static final int OR_NODE = 2;
    public static final int STR_NODE = 3;
    private int nodeType = 0;

    private Node left = null;
    private Node right = null;
    private String keyword = null;

    public Node(int nodeType, Node left, Node right) {
        this.nodeType = nodeType;
        this.left = left;
        this.right = right;
    }

    public Node(int nodeType, String keyword) {
        this.nodeType = nodeType;
        this.keyword = keyword;
    }

    public int getNodeType() {
        return nodeType;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public String getKeyword() {
        return keyword;
    }
}
