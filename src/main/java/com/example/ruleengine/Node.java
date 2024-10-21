package com.example.ruleengine;

// Node.java
public class Node {
    private final String nodeType;  // "operator" or "operand"
    private final String value;      // The actual operator (AND/OR) or operand (like age > 30)
    private final Node left;         // Left child (only applicable for operators)
    private final Node right;        // Right child (only applicable for operators)

    // Constructor for operator nodes (with left and right children)
    public Node(String nodeType, String value, Node left, Node right) {
        this.nodeType = nodeType;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    // Constructor for operand nodes (no children)
    public Node(String nodeType, String value) {
        this(nodeType, value, null, null);
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getValue() {
        return value;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }
}
