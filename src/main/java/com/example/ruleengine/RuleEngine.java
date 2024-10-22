package com.example.ruleengine;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;

public class RuleEngine {
  // Method to create a rule (AST) from JSON-like structure (Map)
  // Method to create rules from JSON-like map
  public static Node createRuleFromJson(Map<String, Object> jsonMap) {
    // Check if the input JSON map is null or missing the required "rules" key
    if (jsonMap == null || !jsonMap.containsKey("rules")) {
        throw new IllegalArgumentException("Invalid JSON structure for rules");
    }

    // Extract the rule map from the JSON structure
    Map<String, Object> rule;
    try {
        rule = (Map<String, Object>) jsonMap.get("rules");
    } catch (ClassCastException e) {
        throw new IllegalArgumentException("Rules must be a valid JSON object", e);
    }

    // Extract the operator and operands, with null checks
    String operator = (String) rule.get("operator");
    if (operator == null) {
        throw new IllegalArgumentException("Operator is missing from the rule");
    }

    // Check and cast left and right operands
    Map<String, Object> leftOperand = getOperand(rule, "left");
    Map<String, Object> rightOperand = getOperand(rule, "right");

    // Create nodes for left and right operands
    Node leftNode = new Node("operand", (String) leftOperand.get("operand"), null, null);
    Node rightNode = new Node("operand", (String) rightOperand.get("operand"), null, null);

    // Return the combined operator node
    return new Node("operator", operator, leftNode, rightNode);
}

// Helper method to safely extract and validate operands
private static Map<String, Object> getOperand(Map<String, Object> rule, String key) {
    Object operand = rule.get(key);
    if (!(operand instanceof Map)) {
        throw new IllegalArgumentException(key + " must be a valid JSON object");
    }
    return (Map<String, Object>) operand;
}


// Helper method to create operand nodes
private Node createOperandNode(Map<String, Object> operandMap) {
    String condition = (String) operandMap.get("condition");
    return new Node("operand", condition, null, null);
}

    // Method to parse the rules from JSON
    private static Node parseRules(JsonNode ruleNode) {
        // If the node has an "operator", it's a complex rule with operands
        if (ruleNode.has("operator")) {
            String operator = ruleNode.get("operator").asText();
            Node leftOperand = parseRules(ruleNode.get("left")); // Fixed key to "left"
            Node rightOperand = parseRules(ruleNode.get("right")); // Fixed key to "right"
            
            return combineRules(leftOperand, rightOperand, operator);
        }
        
        // Otherwise, it's a simple condition (operand)
        String condition = ruleNode.get("operand").asText();
        return createRule(condition);
    }

    // Method to create a rule from a string
    public static Node createRule(String ruleString) {
        // Split the string into tokens while keeping logical operators (AND/OR) intact
        String[] tokens = ruleString.split("\\s+(AND|OR)\\s+");

        Stack<Node> stack = new Stack<>();
        Node lastOperand = null;

        for (String token : tokens) {
            token = token.trim();  // Trim spaces around each token

            // Check if it's a logical operator
            if (token.equals("AND") || token.equals("OR")) {
                if (lastOperand == null || stack.isEmpty()) {
                    throw new IllegalArgumentException("Not enough operands before operator: " + token);
                }
                Node leftOperand = stack.pop();
                stack.push(new Node("operator", token, leftOperand, lastOperand));
                lastOperand = null;  // Reset for the next operand
            } else if (token.matches(".*(=|>|<).*")) {  // If token is an operand (like 'age > 30')
                lastOperand = new Node("operand", token, null, null);
            } else {
                throw new IllegalArgumentException("Invalid token format: " + token);
            }
        }

        // There should be exactly one node remaining in the stack (the root of the AST)
        if (stack.isEmpty() && lastOperand != null) {
            return lastOperand;
        } else if (stack.size() == 1 && lastOperand == null) {
            return stack.pop();
        } else {
            throw new IllegalArgumentException("Invalid rule expression: " + ruleString);
        }
    }

    // Method to combine two rules with a logical operator
    public static Node combineRules(Node rule1, Node rule2, String operator) {
        return new Node("operator", operator, rule1, rule2);
    }

    // Method to combine a list of rules with a logical operator
    public static Node combineRules(List<Node> ruleNodes, String defaultOperator) {
        if (ruleNodes == null || ruleNodes.isEmpty()) {
            throw new IllegalArgumentException("Rule nodes list cannot be null or empty.");
        }

        // Start with the first rule node
        Node combinedNode = ruleNodes.get(0);

        // Iterate over the rest of the rule nodes and combine them
        for (int i = 1; i < ruleNodes.size(); i++) {
            combinedNode = combineRules(combinedNode, ruleNodes.get(i), defaultOperator);
        }

        return combinedNode;
    }

    // Method to evaluate the rule tree against provided data
    public static boolean evaluateRule(Node node, Map<String, Object> data) {
        if (node.getNodeType().equals("operand")) {
            System.out.println("Evaluating operand: " + node.getValue());
            Pattern pattern = Pattern.compile("(\\w+)\\s*(=|>|<)\\s*['\"]?([^'\"]+)['\"]?");
            Matcher matcher = pattern.matcher(node.getValue());

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid operand format: " + node.getValue());
            }

            String field = matcher.group(1);
            String operator = matcher.group(2);
            String value = matcher.group(3).trim();

            Object dataValue = data.get(field);
            if (dataValue instanceof String) {
                value = value.replace("'", "").replace("\"", "");
                switch (operator) {
                    case "=" -> {
                        return dataValue.equals(value);
                    }
                    default -> throw new IllegalArgumentException("Invalid operator for String: " + operator);
                }
            } else if (dataValue instanceof Number) {
                double numericValue = Double.parseDouble(value);  // Convert string value to numeric
                double dataNumericValue = ((Number) dataValue).doubleValue(); // Convert data value to numeric

                switch (operator) {
                    case ">" -> {
                        return dataNumericValue > numericValue;
                    }
                    case "<" -> {
                        return dataNumericValue < numericValue;
                    }
                    case "=" -> {
                        return dataNumericValue == numericValue;
                    }
                    default -> throw new IllegalArgumentException("Unknown operator: " + operator);
                }
            } else {
                throw new IllegalArgumentException("Unsupported data type for field: " + field);
            }
        } else if (node.getNodeType().equals("operator")) {
            boolean leftResult = evaluateRule(node.getLeft(), data);
            boolean rightResult = evaluateRule(node.getRight(), data);
            switch (node.getValue()) {
                case "AND" -> {
                    return leftResult && rightResult;
                }
                case "OR" -> {
                    return leftResult || rightResult;
                }
                default -> throw new IllegalArgumentException("Unknown operator: " + node.getValue());
            }
        }
        return false;
    }
}
