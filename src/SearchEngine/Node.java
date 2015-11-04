package SearchEngine;

/**
 * Created by ting on 2015/11/4.
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

    public int getNodeType() { return nodeType; }

    public Node getLeft() { return left; }

    public Node getRight() { return right; }

    public String getKeyword() { return keyword; }
}
